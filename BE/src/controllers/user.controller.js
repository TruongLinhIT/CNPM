const { User } = require('../models');
const bcrypt = require('bcryptjs');
const { success, failure } = require('../utils/response');

async function getAllUsers(req, res) {
  try {
    const users = await User.findAll({
      attributes: { exclude: ['password_hash'] }
    });
    return success(res, 'Users retrieved successfully', users);
  } catch (error) {
    return failure(res, error.message);
  }
}

async function createUser(req, res) {
  try {
    const { username, password, full_name, role } = req.body;

    const exists = await User.findOne({ where: { username } });
    if (exists) return failure(res, 'Username already exists', null, 400);

    const password_hash = await bcrypt.hash(password, 10);
    const user = await User.create({ username, password_hash, full_name, role });

    const result = user.toJSON();
    delete result.password_hash;

    return success(res, 'User created successfully', result, 201);
  } catch (error) {
    return failure(res, error.message);
  }
}

async function updateUser(req, res) {
  try {
    const { id } = req.params;
    const { full_name, role, password } = req.body;

    const user = await User.findByPk(id);
    if (!user) return failure(res, 'User not found', null, 404);

    const updateData = { full_name, role };
    if (password) {
      updateData.password_hash = await bcrypt.hash(password, 10);
    }

    await user.update(updateData);

    const result = user.toJSON();
    delete result.password_hash;

    return success(res, 'User updated successfully', result);
  } catch (error) {
    return failure(res, error.message);
  }
}

async function deleteUser(req, res) {
  try {
    const { id } = req.params;
    const user = await User.findByPk(id);
    if (!user) return failure(res, 'User not found', null, 404);

    await user.destroy();
    return success(res, 'User deleted successfully');
  } catch (error) {
    return failure(res, error.message);
  }
}

module.exports = {
  getAllUsers,
  createUser,
  updateUser,
  deleteUser
};
