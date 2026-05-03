const { Op } = require('sequelize');
const {
  Order,
  OrderDetail,
  MenuItem,
  DiningTable,
  sequelize,
  User
} = require('../models');

function toNumber(value) {
  const num = Number(value);
  return isNaN(num) ? 0 : num;
}

async function createOrder(payload, userId) {
  const { table_id, items, discount } = payload;

  if (!table_id || !Array.isArray(items) || items.length === 0) {
    const err = new Error('Dữ liệu đơn hàng không hợp lệ (thiếu bàn hoặc món)');
    err.statusCode = 400;
    throw err;
  }

  const table = await DiningTable.findByPk(table_id);
  if (!table) throw new Error('Không tìm thấy bàn ăn');

  // Đảm bảo có userId hợp lệ (Lấy user đầu tiên nếu App không gửi)
  let finalUserId = userId;
  if (!finalUserId) {
    const firstUser = await User.findOne();
    finalUserId = firstUser ? firstUser.user_id : 1;
  }

  const tx = await sequelize.transaction();
  try {
    let subtotal = 0;
    const order = await Order.create({
      user_id: finalUserId,
      table_id,
      subtotal: 0,
      tax: 0,
      discount: toNumber(discount),
      total_amount: 0,
      status: 'Pending'
    }, { transaction: tx });

    const detailPayload = [];
    for (const item of items) {
      const menuItem = await MenuItem.findByPk(item.item_id);
      if (!menuItem) continue;

      const qty = toNumber(item.quantity);
      const price = toNumber(menuItem.price);
      subtotal += price * qty;

      detailPayload.push({
        order_id: order.order_id,
        item_id: item.item_id,
        quantity: qty,
        price_at_time: price,
        status: 'Pending'
      });
    }

    const tax = subtotal * 0.1; // VAT 10%
    const total = Math.max(subtotal + tax - toNumber(discount), 0);

    await OrderDetail.bulkCreate(detailPayload, { transaction: tx });
    await order.update({ subtotal, tax, total_amount: total }, { transaction: tx });
    await table.update({ status: 'Occupied' }, { transaction: tx });

    await tx.commit();
    return order;
  } catch (error) {
    await tx.rollback();
    throw error;
  }
}

async function addItemsToOrder(orderId, itemsPayload) {
  const order = await Order.findByPk(orderId);
  if (!order) throw new Error('Không tìm thấy đơn hàng để thêm món');

  const { items } = itemsPayload;
  const tx = await sequelize.transaction();
  try {
    let extraSubtotal = 0;
    for (const item of items) {
      const menuItem = await MenuItem.findByPk(item.item_id);
      if (!menuItem) continue;

      const price = toNumber(menuItem.price);
      const qty = toNumber(item.quantity);
      extraSubtotal += price * qty;

      await OrderDetail.create({
        order_id: orderId,
        item_id: item.item_id,
        quantity: qty,
        price_at_time: price,
        status: 'Pending'
      }, { transaction: tx });
    }

    const newSubtotal = toNumber(order.subtotal) + extraSubtotal;
    const newTax = newSubtotal * 0.1;
    const newTotal = Math.max(newSubtotal + newTax - toNumber(order.discount), 0);

    await order.update({
      subtotal: newSubtotal,
      tax: newTax,
      total_amount: newTotal
    }, { transaction: tx });

    await tx.commit();
    return order;
  } catch (error) {
    await tx.rollback();
    throw error;
  }
}

async function updateOrderStatus(orderId, status) {
  const order = await Order.findByPk(orderId);
  if (order) await order.update({ status });
  return order;
}

async function updateOrderDetailStatus(detailId, status) {
  const detail = await OrderDetail.findByPk(detailId);
  if (detail) await detail.update({ status });
  return detail;
}

module.exports = { createOrder, addItemsToOrder, updateOrderStatus, updateOrderDetailStatus };
