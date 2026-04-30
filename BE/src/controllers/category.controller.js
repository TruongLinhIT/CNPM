const { Category } = require('../models');
const { success, failure } = require('../utils/response');

async function listCategories(req, res) {
  try {
    const categories = await Category.findAll();
    return success(res, 'Categories fetched', categories);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function getCategory(req, res) {
  try {
    const category = await Category.findByPk(req.params.id);
    if (!category) {
      return failure(res, 'Category not found', null, 404);
    }
    return success(res, 'Category fetched', category);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function createCategory(req, res) {
  try {
    const category = await Category.create(req.body);
    return success(res, 'Category created', category, 201);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function updateCategory(req, res) {
  try {
    const category = await Category.findByPk(req.params.id);
    if (!category) {
      return failure(res, 'Category not found', null, 404);
    }
    await category.update(req.body);
    return success(res, 'Category updated', category);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function deleteCategory(req, res) {
  try {
    const category = await Category.findByPk(req.params.id);
    if (!category) {
      return failure(res, 'Category not found', null, 404);
    }
    await category.destroy();
    return success(res, 'Category deleted', category);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

module.exports = {
  listCategories,
  getCategory,
  createCategory,
  updateCategory,
  deleteCategory
};
