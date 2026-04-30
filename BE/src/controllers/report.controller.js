const { success, failure } = require('../utils/response');
const { getRevenueSummary } = require('../services/report.service');

async function revenueReport(req, res) {
  try {
    const data = await getRevenueSummary(req.query);
    return success(res, 'Revenue report fetched', data);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

module.exports = { revenueReport };
