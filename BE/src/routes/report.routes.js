const express = require('express');
const { revenueReport } = require('../controllers/report.controller');

const router = express.Router();

// Gỡ bỏ authenticate để chạy đơn giản theo yêu cầu
router.get('/revenue', revenueReport);

module.exports = router;
