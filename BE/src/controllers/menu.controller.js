const { MenuItem, Category } = require('../models');
const { success, failure } = require('../utils/response');

async function listMenuItems(req, res) {
  try {
    const items = await MenuItem.findAll({ include: [Category] });
    return success(res, 'Menu items fetched', items);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function getMenuItem(req, res) {
  try {
    const item = await MenuItem.findByPk(req.params.id, { include: [Category] });
    if (!item) {
      return failure(res, 'Menu item not found', null, 404);
    }
    return success(res, 'Menu item fetched', item);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function createMenuItem(req, res) {
  try {
    const item = await MenuItem.create(req.body);
    return success(res, 'Menu item created', item, 201);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function updateMenuItem(req, res) {
  try {
    const item = await MenuItem.findByPk(req.params.id);
    if (!item) {
      return failure(res, 'Menu item not found', null, 404);
    }
    await item.update(req.body);
    return success(res, 'Menu item updated', item);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function deleteMenuItem(req, res) {
  try {
    const item = await MenuItem.findByPk(req.params.id);
    if (!item) {
      return failure(res, 'Menu item not found', null, 404);
    }
    await item.destroy();
    return success(res, 'Menu item deleted', item);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

module.exports = {
  listMenuItems,
  getMenuItem,
  createMenuItem,
  updateMenuItem,
  deleteMenuItem
};
