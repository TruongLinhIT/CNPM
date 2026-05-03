const { Payment, Order, DiningTable } = require('../models');

async function createPayment(payload) {
  const { order_id, amount_paid, payment_method } = payload;

  // Senior Fix: Kiểm tra undefined/null thay vì dùng ! để cho phép giá trị 0
  if (order_id === undefined || amount_paid === undefined || !payment_method) {
    const err = new Error('Missing payment fields (order_id, amount_paid, payment_method)');
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

  // Cập nhật trạng thái đơn hàng và giải phóng bàn
  await order.update({ status: 'Paid' });

  const table = await DiningTable.findByPk(order.table_id);
  if (table) {
    await table.update({ status: 'Available' });
  }

  return payment;
}

module.exports = { createPayment };
