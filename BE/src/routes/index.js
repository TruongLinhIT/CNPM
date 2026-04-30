const express = require('express');

const authRoutes = require('./auth.routes');
const categoryRoutes = require('./category.routes');
const menuRoutes = require('./menu.routes');
const tableRoutes = require('./table.routes');
const orderRoutes = require('./order.routes');
const paymentRoutes = require('./payment.routes');
const reportRoutes = require('./report.routes');
const userRoutes = require('./user.routes');

const router = express.Router();

router.use('/auth', authRoutes);
router.use('/categories', categoryRoutes);
router.use('/menu', menuRoutes); // Đổi từ /menu-items thành /menu để khớp với yêu cầu và fix lỗi 404
router.use('/tables', tableRoutes);
router.use('/orders', orderRoutes);
router.use('/payments', paymentRoutes);
router.use('/reports', reportRoutes);
router.use('/users', userRoutes);

module.exports = router;
