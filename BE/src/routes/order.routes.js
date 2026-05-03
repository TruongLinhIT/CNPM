const express = require('express');
const {
  createOrderController,
  addOrderItemsController,
  listOrders,
  listActiveOrdersController,
  getOrder,
  updateOrderStatusController,
  updateOrderDetailStatusController
} = require('../controllers/order.controller');

const router = express.Router();

// THỨ TỰ QUAN TRỌNG: Route tĩnh (/active) phải nằm TRƯỚC route động (/:id)
router.get('/', listOrders);
router.get('/active', listActiveOrdersController);
router.get('/:id', getOrder);

router.post('/', createOrderController);
router.post('/:id/items', addOrderItemsController); // Endpoint cho chức năng Thêm món

router.put('/:id/status', updateOrderStatusController);
router.put('/items/:detailId/status', updateOrderDetailStatusController);

module.exports = router;
