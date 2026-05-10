const { Payment, Order, DiningTable, sequelize } = require('../models');
const { getIO } = require('../utils/socket');

async function createPayment(payload) {
  const { order_id, amount_paid, payment_method } = payload;

  const numericOrderId = parseInt(order_id);
  console.log(`\n[SENIOR LOG] >>> Bắt đầu giải phóng bàn cho Đơn hàng: ${numericOrderId}`);

  const t = await sequelize.transaction();

  try {
    // 1. Tìm Order (KHÔNG dùng raw: true để lấy Instance có hàm update)
    const order = await Order.findByPk(numericOrderId, { transaction: t });

    if (!order) {
      console.error(`[SENIOR ERROR] - Không tìm thấy đơn hàng ID: ${numericOrderId}`);
      throw new Error(`Order not found: ${numericOrderId}`);
    }

    // Lấy table_id từ Instance
    const tableId = order.table_id;
    console.log(`[SENIOR DEBUG] - Table ID xác định: ${tableId}. Trạng thái hiện tại: ${order.status}`);

    // 2. Tạo/Tìm bản ghi Payment
    await Payment.findOrCreate({
      where: { order_id: numericOrderId, payment_method: payment_method },
      defaults: {
        amount_paid: parseFloat(amount_paid),
        payment_method
      },
      transaction: t
    });

    // 3. Cập nhật trạng thái đơn hàng (Dùng Order.update tĩnh để an toàn hơn)
    await Order.update(
        { status: 'Paid' },
        { where: { order_id: numericOrderId }, transaction: t }
    );
    console.log(`[SENIOR SUCCESS] - Đã chuyển trạng thái Order ${numericOrderId} sang 'Paid'`);

    // 4. GIẢI PHÓNG BÀN - Cập nhật trực tiếp vào bảng DiningTables
    if (tableId) {
      const [updatedRows] = await DiningTable.update(
        { status: 'Available' },
        {
          where: { table_id: tableId },
          transaction: t
        }
      );

      if (updatedRows > 0) {
        console.log(`[SENIOR SUCCESS] - Bàn ${tableId} đã chuyển sang trạng thái 'Available'.`);

        // --- REAL-TIME PUSH ---
        try {
          const io = getIO();
          io.emit('table_status_changed', {
            table_id: tableId,
            status: 'Available',
            order_id: numericOrderId
          });
        } catch (socketErr) {
          console.warn('[SOCKET WARNING] - Không thể phát tín hiệu real-time.');
        }
      } else {
        console.warn(`[SENIOR WARNING] - Không tìm thấy bản ghi bàn ID ${tableId} để cập nhật.`);
      }
    }

    await t.commit();
    console.log(`[SENIOR LOG] <<< HOÀN TẤT QUY TRÌNH CHO ORDER: ${numericOrderId}\n`);
    return { success: true };

  } catch (error) {
    await t.rollback();
    console.error(`[SENIOR CRITICAL ERROR] - Lỗi rồi: ${error.message}`);
    throw error;
  }
}

module.exports = { createPayment };
