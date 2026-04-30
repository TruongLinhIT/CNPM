const express = require('express');
const { login } = require('../controllers/auth.controller');

const router = express.Router();

// Chỉ giữ lại login, register sẽ được chuyển sang quản lý bởi Admin qua route /users
router.post('/login', login);

module.exports = router;
