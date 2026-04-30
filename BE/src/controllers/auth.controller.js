const { success, failure } = require('../utils/response');
const { loginUser } = require('../services/auth.service');

async function login(req, res) {
  try {
    const result = await loginUser(req.body);
    // Trả về trực tiếp thông tin user sau khi đăng nhập thành công
    return success(res, 'Login successful', result);
  } catch (error) {
    return failure(res, error.message, error.details || null, error.statusCode || 500);
  }
}

module.exports = { login };
