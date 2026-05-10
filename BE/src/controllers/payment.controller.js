const { Payment, Order, DiningTable } = require('../models');
const { success, failure } = require('../utils/response');
const { createPayment } = require('../services/payment.service');
const { sortObject } = require('../utils/vnpay');
const moment = require('moment');
const crypto = require('crypto');
const queryString = require('qs');

async function createPaymentController(req, res) {
  try {
    const payment = await createPayment(req.body);
    return success(res, 'Payment created', payment, 201);
  } catch (error) {
    return failure(res, error.message, error.details || null, error.statusCode || 500);
  }
}

async function listPayments(req, res) {
  try {
    const payments = await Payment.findAll({ include: [Order] });
    return success(res, 'Payments fetched', payments);
  } catch (error) {
    return failure(res, error.message, null, 500);
  }
}

async function createVnpayUrl(req, res) {
  try {
    let { order_id, amount, bankCode } = req.body;

    const tmnCode = process.env.VNP_TMN_CODE;
    const secretKey = process.env.VNP_HASH_SECRET;
    let vnpUrl = process.env.VNP_URL;
    const returnUrl = process.env.VNP_RETURN_URL;

    const date = new Date();
    const createDate = moment(date).format('YYYYMMDDHHmmss');

    let ipAddr = req.headers['x-forwarded-for'] ||
        req.connection.remoteAddress ||
        req.socket.remoteAddress ||
        req.connection.socket.remoteAddress;

    if (ipAddr === '::1' || ipAddr === '127.0.0.1') {
      ipAddr = '1.1.1.1';
    }

    let vnp_Params = {};
    vnp_Params['vnp_Version'] = '2.1.0';
    vnp_Params['vnp_Command'] = 'pay';
    vnp_Params['vnp_TmnCode'] = tmnCode;
    vnp_Params['vnp_Locale'] = 'vn';
    vnp_Params['vnp_CurrCode'] = 'VND';
    vnp_Params['vnp_TxnRef'] = order_id + '_' + createDate;
    vnp_Params['vnp_OrderInfo'] = 'Thanh toan don hang ' + order_id;
    vnp_Params['vnp_OrderType'] = 'other';
    vnp_Params['vnp_Amount'] = Math.floor(parseInt(amount) * 100);
    vnp_Params['vnp_ReturnUrl'] = returnUrl;
    vnp_Params['vnp_IpAddr'] = ipAddr;
    vnp_Params['vnp_CreateDate'] = createDate;

    if (bankCode) {
      vnp_Params['vnp_BankCode'] = bankCode;
    }

    vnp_Params = sortObject(vnp_Params);

    const signData = queryString.stringify(vnp_Params, { encode: false });
    const hmac = crypto.createHmac("sha512", secretKey);
    const signed = hmac.update(new Buffer.from(signData, 'utf-8')).digest("hex");

    vnp_Params['vnp_SecureHash'] = signed;
    vnpUrl += '?' + queryString.stringify(vnp_Params, { encode: false });

    return success(res, 'VNPAY URL created', { url: vnpUrl });
  } catch (error) {
    return failure(res, error.message);
  }
}

async function processVnpayTransaction(vnp_Params) {
    const responseCode = vnp_Params['vnp_ResponseCode'];
    const txnRef = vnp_Params['vnp_TxnRef'];
    const order_id = txnRef.split('_')[0];

    if (responseCode === "00") {
        // Senior Fix: Loại bỏ check status Paid ở đây.
        // Hãy để Service xử lý Idempotency (gọi nhiều lần không sao)
        // để đảm bảo Bàn LUÔN được giải phóng.
        await createPayment({
            order_id: order_id,
            amount_paid: vnp_Params['vnp_Amount'] / 100,
            payment_method: 'VNPAY'
        });
        return { success: true, message: 'Xử lý giao dịch thành công' };
    }
    return { success: false, message: 'Giao dịch thất bại: ' + responseCode };
}

async function vnpayReturn(req, res) {
  try {
    let vnp_Params = req.query;
    const secureHash = vnp_Params['vnp_SecureHash'];

    delete vnp_Params['vnp_SecureHash'];
    delete vnp_Params['vnp_SecureHashType'];

    vnp_Params = sortObject(vnp_Params);

    const secretKey = process.env.VNP_HASH_SECRET;
    const signData = queryString.stringify(vnp_Params, { encode: false });
    const hmac = crypto.createHmac("sha512", secretKey);
    const signed = hmac.update(new Buffer.from(signData, 'utf-8')).digest("hex");

    if (secureHash === signed) {
      const result = await processVnpayTransaction(vnp_Params);
      if (result.success) {
        return res.send(`<html><body style="text-align:center; font-family: sans-serif; padding-top: 50px;">
            <div style="color:#2ecc71;">
                <h2>Thanh toán thành công!</h2>
            </div>
            <script>setTimeout(function(){ window.close(); }, 3000);</script>
        </body></html>`);
      } else {
        return res.send(`<h1>Thanh toán không thành công</h1><p>${result.message}</p>`);
      }
    } else {
      return res.status(400).send('Chữ ký không hợp lệ');
    }
  } catch (error) {
    console.error('VNPAY Return Error:', error);
    return res.status(500).send(error.message);
  }
}

async function vnpayIpn(req, res) {
    try {
        let vnp_Params = req.query;
        let secureHash = vnp_Params['vnp_SecureHash'];
        delete vnp_Params['vnp_SecureHash'];
        delete vnp_Params['vnp_SecureHashType'];
        vnp_Params = sortObject(vnp_Params);
        let secretKey = process.env.VNP_HASH_SECRET;
        let signData = queryString.stringify(vnp_Params, { encode: false });
        let hmac = crypto.createHmac("sha512", secretKey);
        let signed = hmac.update(new Buffer.from(signData, 'utf-8')).digest("hex");

        if (secureHash === signed) {
            await processVnpayTransaction(vnp_Params);
            res.status(200).json({ RspCode: '00', Message: 'Confirm Success' });
        } else {
            res.status(200).json({ RspCode: '97', Message: 'Fail checksum' });
        }
    } catch (error) {
        res.status(200).json({ RspCode: '99', Message: 'Unknown error' });
    }
}

module.exports = {
  createPaymentController,
  listPayments,
  createVnpayUrl,
  vnpayReturn,
  vnpayIpn
};
