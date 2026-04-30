const { Payment, Order } = require('../models');
const { success, failure } = require('../utils/response');
const { createPayment } = require('../services/payment.service');

async function createPaymentController(req, res) {
  try {
    const payment = await createPayment(req.body);
    return success(res, 'Payment created', payment, 201);
  } catch (error) {
    return failure(res, error.message, error.details || null, error.statusCode || 500);
  }
}

async function listPayments(req, res) {
  try {
    const payments = await Payment.findAll({ include: [Order] });
    return success(res, 'Payments fetched', payments);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

module.exports = { createPaymentController, listPayments };
