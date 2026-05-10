const express = require('express');
const {
  createPaymentController,
  listPayments,
  createVnpayUrl,
  vnpayReturn,
  vnpayIpn
} = require('../controllers/payment.controller');

const router = express.Router();

router.get('/', listPayments);
router.post('/', createPaymentController);

// VNPAY Routes
router.post('/create_vnpay_url', createVnpayUrl);
router.get('/vnpay_return', vnpayReturn);
router.get('/vnpay_ipn', vnpayIpn);

module.exports = router;
