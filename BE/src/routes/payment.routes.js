const express = require('express');
const {
  createPaymentController,
  listPayments
} = require('../controllers/payment.controller');

const router = express.Router();

// Tạm gỡ bỏ authenticate để chạy đơn giản theo yêu cầu
router.get('/', listPayments);
router.post('/', createPaymentController);

module.exports = router;
