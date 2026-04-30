const { failure } = require('../utils/response');

function notFound(req, res) {
  return failure(res, 'Route not found', null, 404);
}

module.exports = { notFound };
