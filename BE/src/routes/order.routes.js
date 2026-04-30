const express = require('express');
const {
  createOrderController,
  listOrders,
  getOrder,
  updateOrderStatusController,
  updateOrderDetailStatusController
} = require('../controllers/order.controller');
const { authenticate, requireRoles } = require('../middlewares/auth.middleware');

const router = express.Router();

router.get('/', authenticate, listOrders);
router.get('/:id', authenticate, getOrder);
router.post(
  '/',
  authenticate,
  requireRoles('Waitstaff', 'Manager'),
  createOrderController
);
router.patch(
  '/:id/status',
  authenticate,
  requireRoles('Kitchen', 'Waitstaff', 'Manager'),
  updateOrderStatusController
);
router.patch(
  '/items/:detailId/status',
  authenticate,
  requireRoles('Kitchen', 'Waitstaff', 'Manager'),
  updateOrderDetailStatusController
);

module.exports = router;
