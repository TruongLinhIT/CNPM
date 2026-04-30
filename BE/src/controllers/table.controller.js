const { DiningTable } = require('../models');
const { success, failure } = require('../utils/response');

async function listTables(req, res) {
  try {
    const tables = await DiningTable.findAll();
    return success(res, 'Tables fetched', tables);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function getTable(req, res) {
  try {
    const table = await DiningTable.findByPk(req.params.id);
    if (!table) {
      return failure(res, 'Table not found', null, 404);
    }
    return success(res, 'Table fetched', table);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function createTable(req, res) {
  try {
    const table = await DiningTable.create(req.body);
    return success(res, 'Table created', table, 201);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function updateTable(req, res) {
  try {
    const table = await DiningTable.findByPk(req.params.id);
    if (!table) {
      return failure(res, 'Table not found', null, 404);
    }
    await table.update(req.body);
    return success(res, 'Table updated', table);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function deleteTable(req, res) {
  try {
    const table = await DiningTable.findByPk(req.params.id);
    if (!table) {
      return failure(res, 'Table not found', null, 404);
    }
    await table.destroy();
    return success(res, 'Table deleted', table);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

module.exports = {
  listTables,
  getTable,
  createTable,
  updateTable,
  deleteTable
};
