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
router.put('/:id', authenticate, requireRoles('Manager'), updateTable);
router.delete('/:id', authenticate, requireRoles('Manager'), deleteTable);

module.exports = router;
