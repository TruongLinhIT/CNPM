const { Op } = require('sequelize');
const { Order, Payment } = require('../models');

async function getRevenueSummary(query) {
  const { start_date, end_date } = query;

  const where = {};
  if (start_date || end_date) {
    where.payment_time = {};
    if (start_date) {
      where.payment_time[Op.gte] = new Date(start_date);
    }
    if (end_date) {
      where.payment_time[Op.lte] = new Date(end_date);
    }
  }

  const totalRevenue = await Payment.sum('amount_paid', { where });
  const paidOrders = await Order.count({ where: { status: 'Paid' } });

  return {
    total_revenue: Number(totalRevenue || 0),
    paid_orders: paidOrders,
    from: start_date || null,
    to: end_date || null
  };
}

module.exports = { getRevenueSummary };
