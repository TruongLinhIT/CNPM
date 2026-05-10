require('dotenv').config();
const http = require('http');

const app = require('./app');
const { initSocket } = require('./utils/socket');
const { sequelize } = require('./config/database');

const PORT = process.env.PORT || 3000;

const server = http.createServer(app);
initSocket(server);

async function startServer() {
  try {
    await sequelize.authenticate();
    console.log('Database connected.');

    // Senior Fix: Sử dụng alter: true để Sequelize tự động cập nhật các cột (như ENUM 'VNPAY')
    // mà không làm mất dữ liệu cũ.
    if (process.env.DB_SYNC === 'true') {
      await sequelize.sync({ alter: true });
      console.log('Database synchronized (altered).');
    }

    server.listen(PORT, () => {
      console.log(`Server running on port ${PORT}`);
    });
  } catch (error) {
    console.error('Failed to start server:', error);
    process.exit(1);
  }
}

startServer();
