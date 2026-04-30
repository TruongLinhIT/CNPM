const express = require('express');
const { revenueReport } = require('../controllers/report.controller');
const { authenticate, requireRoles } = require('../middlewares/auth.middleware');

const router = express.Router();

router.get('/revenue', authenticate, requireRoles('Manager'), revenueReport);

module.exports = router;
