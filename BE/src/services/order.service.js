const { Op } = require('sequelize');
const {
  Order,
  OrderDetail,
  MenuItem,
  DiningTable,
  sequelize
} = require('../models');
const { getIO } = require('../utils/socket');

function toNumber(value) {
  const numberValue = Number(value);
  return Number.isNaN(numberValue) ? 0 : numberValue;
}

async function createOrder(payload, userId) {
  const { table_id, items, discount } = payload;

  if (!table_id || !Array.isArray(items) || items.length === 0) {
    const err = new Error('Invalid order payload');
    err.statusCode = 400;
    throw err;
  }

  const table = await DiningTable.findByPk(table_id);
  if (!table) {
    const err = new Error('Dining table not found');
    err.statusCode = 404;
    throw err;
  }

  const itemIds = items.map((item) => item.item_id);
  const uniqueItemIds = Array.from(new Set(itemIds));
  const menuItems = await MenuItem.findAll({
    where: { item_id: { [Op.in]: uniqueItemIds } }
  });

  if (menuItems.length !== uniqueItemIds.length) {
    const err = new Error('One or more menu items not found');
    err.statusCode = 404;
    throw err;
  }

  const menuMap = new Map(menuItems.map((item) => [item.item_id, item]));

  const tx = await sequelize.transaction();
  try {
    let subtotal = 0;

    const order = await Order.create(
      {
        user_id: userId,
        table_id,
        subtotal: 0,
        tax: 0,
        discount: toNumber(discount),
        total_amount: 0,
        status: 'Pending'
      },
      { transaction: tx }
    );

    const detailPayload = items.map((item) => {
      const menuItem = menuMap.get(item.item_id);
      const quantity = toNumber(item.quantity || 1);
      if (quantity <= 0) {
        const err = new Error('Item quantity must be greater than zero');
        err.statusCode = 400;
        throw err;
      }
      const priceAtTime = toNumber(menuItem.price);
      subtotal += priceAtTime * quantity;

      return {
        order_id: order.order_id,
        item_id: item.item_id,
        quantity,
        price_at_time: priceAtTime,
        notes: item.notes || null,
        status: 'Pending'
      };
    });

    const taxRate = toNumber(process.env.TAX_RATE || 0);
    const tax = subtotal * taxRate;
    const discountValue = toNumber(discount);
    const totalAmount = Math.max(subtotal + tax - discountValue, 0);

    await OrderDetail.bulkCreate(detailPayload, { transaction: tx });

    await order.update(
      {
        subtotal,
        tax,
        discount: discountValue,
        total_amount: totalAmount
      },
      { transaction: tx }
    );

    if (table.status !== 'Occupied') {
      await table.update({ status: 'Occupied' }, { transaction: tx });
    }

    await tx.commit();

    try {
      getIO().emit('order:new', { order_id: order.order_id, table_id });
    } catch (socketError) {}

    return order;
  } catch (error) {
    await tx.rollback();
    throw error;
  }
}

async function updateOrderStatus(orderId, status) {
  const order = await Order.findByPk(orderId);
  if (!order) {
    const err = new Error('Order not found');
    err.statusCode = 404;
    throw err;
  }

  await order.update({ status });

  try {
    getIO().emit('order:status', { order_id: order.order_id, status });
  } catch (socketError) {}

  return order;
}

async function updateOrderDetailStatus(detailId, status) {
  const detail = await OrderDetail.findByPk(detailId);
  if (!detail) {
    const err = new Error('Order detail not found');
    err.statusCode = 404;
    throw err;
  }

  await detail.update({ status });

  try {
    getIO().emit('order:item-status', {
      order_id: detail.order_id,
      order_detail_id: detail.order_detail_id,
      status
    });
  } catch (socketError) {}

  return detail;
}

module.exports = { createOrder, updateOrderStatus, updateOrderDetailStatus };
