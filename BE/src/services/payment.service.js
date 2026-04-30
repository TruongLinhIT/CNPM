const { Payment, Order, DiningTable } = require('../models');

async function createPayment(payload) {
  const { order_id, amount_paid, payment_method } = payload;

  if (!order_id || !amount_paid || !payment_method) {
    const err = new Error('Missing payment fields');
    err.statusCode = 400;
    throw err;
  }

  if (Number(amount_paid) <= 0) {
    const err = new Error('Amount paid must be greater than zero');
    err.statusCode = 400;
    throw err;
  }

  const order = await Order.findByPk(order_id);
  if (!order) {
    const err = new Error('Order not found');
    err.statusCode = 404;
    throw err;
  }

  const payment = await Payment.create({
    order_id,
    amount_paid,
    payment_method
  });

  await order.update({ status: 'Paid' });

  const table = await DiningTable.findByPk(order.table_id);
  if (table) {
    await table.update({ status: 'Available' });
  }

  return payment;
}

module.exports = { createPayment };
