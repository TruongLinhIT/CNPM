const { Order, OrderDetail, MenuItem, DiningTable, User } = require('../models');
const { success, failure } = require('../utils/response');
const {
  createOrder,
  updateOrderStatus,
  updateOrderDetailStatus
} = require('../services/order.service');

async function createOrderController(req, res) {
  try {
    // Vì không dùng JWT, ta lấy user_id từ body hoặc một giá trị mặc định nếu không có
    // FE nên gửi kèm user_id của người tạo order (đã lưu sau khi login)
    const userId = req.body.user_id || (req.user ? req.user.user_id : null);

    if (!userId) {
      return failure(res, 'User ID is required to create an order', null, 400);
    }

    const order = await createOrder(req.body, userId);
    return success(res, 'Order created', order, 201);
  } catch (error) {
    return failure(res, error.message, error.details || null, error.statusCode || 500);
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
    return failure(res, error.message, error.details || null, error.statusCode || 500);
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
    return failure(res, error.message, error.details || null, error.statusCode || 500);
  }
}

module.exports = {
  createOrderController,
  listOrders,
  getOrder,
  updateOrderStatusController,
  updateOrderDetailStatusController
};
