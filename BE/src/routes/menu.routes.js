const express = require('express');
const {
  listMenuItems,
  getMenuItem,
  createMenuItem,
  updateMenuItem,
  deleteMenuItem
} = require('../controllers/menu.controller');
const { authenticate, requireRoles } = require('../middlewares/auth.middleware');

const router = express.Router();

router.get('/', authenticate, listMenuItems);
router.get('/:id', authenticate, getMenuItem);
router.post('/', authenticate, requireRoles('Manager'), createMenuItem);
router.put('/:id', authenticate, requireRoles('Manager'), updateMenuItem);
router.delete('/:id', authenticate, requireRoles('Manager'), deleteMenuItem);

module.exports = router;
