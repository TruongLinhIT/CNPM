const express = require('express');
const {
  createPaymentController,
  listPayments
} = require('../controllers/payment.controller');
const { authenticate, requireRoles } = require('../middlewares/auth.middleware');

const router = express.Router();

router.get('/', authenticate, requireRoles('Manager'), listPayments);
router.post('/', authenticate, requireRoles('Waitstaff', 'Manager'), createPaymentController);

module.exports = router;
