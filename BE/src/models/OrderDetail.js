const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
  return sequelize.define(
    'OrderDetail',
    {
      order_detail_id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
      },
      order_id: {
        type: DataTypes.INTEGER,
        allowNull: false
      },
      item_id: {
        type: DataTypes.INTEGER,
        allowNull: false
      },
      quantity: {
        type: DataTypes.INTEGER,
        allowNull: false,
        defaultValue: 1
      },
      price_at_time: {
        type: DataTypes.DECIMAL(10, 2),
        allowNull: false
      },
      notes: {
        type: DataTypes.STRING(255),
        allowNull: true
      },
      status: {
        type: DataTypes.ENUM(
          'Pending',
          'Preparing',
          'Ready',
          'Served',
          'Cancelled'
        ),
        allowNull: false,
        defaultValue: 'Pending'
      }
    },
    {
      tableName: 'OrderDetails',
      timestamps: false
    }
  );
};
