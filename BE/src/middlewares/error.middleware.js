const { failure } = require('../utils/response');

function errorHandler(err, req, res, next) {
  const statusCode = err.statusCode || 500;
  const message = err.message || 'Internal server error';

  return failure(res, message, err.details || null, statusCode);
}

module.exports = { errorHandler };
