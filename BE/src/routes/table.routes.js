const express = require('express');
const {
  listTables,
  getTable,
  createTable,
  updateTable,
  deleteTable
} = require('../controllers/table.controller');
const { authenticate, requireRoles } = require('../middlewares/auth.middleware');

const router = express.Router();

router.get('/', authenticate, listTables);
router.get('/:id', authenticate, getTable);
router.post('/', authenticate, requireRoles('Manager'), createTable);

// Cập nhật trạng thái bàn - Cho phép cả Manager và Waitstaff
router.put('/:id/status', authenticate, requireRoles('Manager', 'Waitstaff'), updateTable);

router.put('/:id', authenticate, requireRoles('Manager'), updateTable);
router.delete('/:id', authenticate, requireRoles('Manager'), deleteTable);

module.exports = router;
