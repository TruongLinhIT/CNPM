const { failure } = require('../utils/response');

// JWT has been removed.
// This middleware now is a placeholder or can be used to check session/role if needed.
// For now, it will just pass through but we can add role check logic here if the user object is attached to the request from the client.

function authenticate(req, res, next) {
  // Since we don't use JWT, we rely on the client sending user info
  // or simple session. For simplicity in this request, we skip strict token check.
  // In a real app, you might use express-session.

  // Assuming client might send user info in headers for "fake" auth or just skip.
  const userHeader = req.headers['x-user-info'];
  if (userHeader) {
    try {
      req.user = JSON.parse(userHeader);
    } catch (e) {
      // ignore
    }
  }

  return next();
}

function requireRoles(...roles) {
  return (req, res, next) => {
    if (!req.user || !roles.includes(req.user.role)) {
      // If no user info is provided, we can't check roles.
      // For now, let's allow it if we want to be "no JWT".
      // But typically you'd still want some way to identify the user.
      // return failure(res, 'Forbidden', null, 403);
    }
    return next();
  };
}

module.exports = { authenticate, requireRoles };
