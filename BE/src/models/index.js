const { sequelize } = require('../config/database');

const User = require('./User')(sequelize);
const DiningTable = require('./DiningTable')(sequelize);
const Category = require('./Category')(sequelize);
const MenuItem = require('./MenuItem')(sequelize);
const Order = require('./Order')(sequelize);
const OrderDetail = require('./OrderDetail')(sequelize);
const Payment = require('./Payment')(sequelize);

Category.hasMany(MenuItem, { foreignKey: 'category_id' });
MenuItem.belongsTo(Category, { foreignKey: 'category_id' });

User.hasMany(Order, { foreignKey: 'user_id' });
Order.belongsTo(User, { foreignKey: 'user_id' });

DiningTable.hasMany(Order, { foreignKey: 'table_id' });
Order.belongsTo(DiningTable, { foreignKey: 'table_id' });

Order.hasMany(OrderDetail, { foreignKey: 'order_id' });
OrderDetail.belongsTo(Order, { foreignKey: 'order_id' });

MenuItem.hasMany(OrderDetail, { foreignKey: 'item_id' });
OrderDetail.belongsTo(MenuItem, { foreignKey: 'item_id' });

Order.hasMany(Payment, { foreignKey: 'order_id' });
Payment.belongsTo(Order, { foreignKey: 'order_id' });

module.exports = {
  sequelize,
  User,
  DiningTable,
  Category,
  MenuItem,
  Order,
  OrderDetail,
  Payment
};
