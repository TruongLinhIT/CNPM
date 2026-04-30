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
    if (process.env.DB_SYNC === 'true') {
      await sequelize.sync();
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
