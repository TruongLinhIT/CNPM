const { Order, OrderDetail, MenuItem, DiningTable, User } = require('../models');
const { success, failure } = require('../utils/response');
const { Op } = require('sequelize');
const {
  createOrder,
  addItemsToOrder,
  updateOrderStatus,
  updateOrderDetailStatus
} = require('../services/order.service');

async function createOrderController(req, res) {
  try {
    // Senior Fix: Nếu không có user_id gửi lên, mặc định lấy user đầu tiên
    // để đơn giản hóa phần đăng nhập theo yêu cầu của bạn.
    let userId = req.body.user_id || (req.user ? req.user.user_id : null);

    if (!userId) {
      const firstUser = await User.findOne();
      userId = firstUser ? firstUser.user_id : 1;
    }

    const order = await createOrder(req.body, userId);
    return success(res, 'Order created', order, 201);
  } catch (error) {
    return failure(res, error.message, null, error.statusCode || 500);
  }
}

async function addOrderItemsController(req, res) {
  try {
    const order = await addItemsToOrder(req.params.id, req.body);
    return success(res, 'Items added to order', order);
  } catch (error) {
    return failure(res, error.message, null, error.statusCode || 500);
  }
}

async function listActiveOrdersController(req, res) {
  try {
    const orders = await Order.findAll({
      where: {
        status: { [Op.notIn]: ['Paid', 'Cancelled'] }
      },
      include: [
        { model: User },
        { model: DiningTable },
        { model: OrderDetail, include: [MenuItem] }
      ]
    });
    return success(res, 'Active orders fetched', orders);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function listOrders(req, res) {
  try {
    const orders = await Order.findAll({
      include: [
        { model: User },
        { model: DiningTable },
        { model: OrderDetail, include: [MenuItem] }
      ]
    });
    return success(res, 'Orders fetched', orders);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function getOrder(req, res) {
  try {
    const order = await Order.findByPk(req.params.id, {
      include: [
        { model: User },
        { model: DiningTable },
        { model: OrderDetail, include: [MenuItem] }
      ]
    });
    if (!order) {
      return failure(res, 'Order not found', null, 404);
    }
    return success(res, 'Order fetched', order);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function updateOrderStatusController(req, res) {
  try {
    const order = await updateOrderStatus(req.params.id, req.body.status);
    return success(res, 'Order status updated', order);
  } catch (error) {
    return failure(res, error.message, null, error.statusCode || 500);
  }
}

async function updateOrderDetailStatusController(req, res) {
  try {
    const detail = await updateOrderDetailStatus(
      req.params.detailId,
      req.body.status
    );
    return success(res, 'Order item status updated', detail);
  } catch (error) {
    return failure(res, error.message, null, error.statusCode || 500);
  }
}

module.exports = {
  createOrderController,
  addOrderItemsController,
  listOrders,
  listActiveOrdersController,
  getOrder,
  updateOrderStatusController,
  updateOrderDetailStatusController
};
