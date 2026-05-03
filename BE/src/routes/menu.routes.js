const express = require('express');
const {
  listMenuItems,
  getMenuItem,
  createMenuItem,
  updateMenuItem,
  deleteMenuItem
} = require('../controllers/menu.controller');

const router = express.Router();

// Gỡ bỏ authenticate để chạy đơn giản theo yêu cầu của bạn
router.get('/', listMenuItems);
router.get('/:id', getMenuItem);
router.post('/', createMenuItem);
router.put('/:id', updateMenuItem);
router.delete('/:id', deleteMenuItem);

module.exports = router;
