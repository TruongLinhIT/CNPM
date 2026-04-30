const express = require('express');
const {
  listCategories,
  getCategory,
  createCategory,
  updateCategory,
  deleteCategory
} = require('../controllers/category.controller');
const { authenticate, requireRoles } = require('../middlewares/auth.middleware');

const router = express.Router();

router.get('/', authenticate, listCategories);
router.get('/:id', authenticate, getCategory);
router.post('/', authenticate, requireRoles('Manager'), createCategory);
router.put('/:id', authenticate, requireRoles('Manager'), updateCategory);
router.delete('/:id', authenticate, requireRoles('Manager'), deleteCategory);

module.exports = router;
