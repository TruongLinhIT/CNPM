const express = require('express');
const {
  listTables,
  getTable,
  createTable,
  updateTable,
  deleteTable
} = require('../controllers/table.controller');

const router = express.Router();

// Gỡ bỏ authenticate để chạy đơn giản theo yêu cầu
router.get('/', listTables);
router.get('/:id', getTable);
router.post('/', createTable);
router.put('/:id/status', updateTable);
router.put('/:id', updateTable);
router.delete('/:id', deleteTable);

module.exports = router;
