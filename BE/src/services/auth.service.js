const bcrypt = require('bcryptjs');
const { User } = require('../models');

function sanitizeUser(user) {
  return {
    user_id: user.user_id,
    username: user.username,
    full_name: user.full_name,
    role: user.role,
    created_at: user.created_at
  };
}

async function loginUser(payload) {
  const { username, password } = payload;

  if (!username || !password) {
    const err = new Error('Missing credentials');
    err.statusCode = 400;
    throw err;
  }

  const user = await User.findOne({ where: { username } });
  if (!user) {
    const err = new Error('Invalid credentials');
    err.statusCode = 401;
    throw err;
  }

  const match = await bcrypt.compare(password, user.password_hash);
  if (!match) {
    const err = new Error('Invalid credentials');
    err.statusCode = 401;
    throw err;
  }

  // Removed JWT generation as requested.
  // Returning only user info.
  return { user: sanitizeUser(user) };
}

module.exports = { loginUser };
