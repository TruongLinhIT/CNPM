const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
  return sequelize.define(
    'Payment',
    {
      payment_id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
      },
      order_id: {
        type: DataTypes.INTEGER,
        allowNull: false
      },
      amount_paid: {
        type: DataTypes.DECIMAL(10, 2),
        allowNull: false
      },
      payment_method: {
        // Senior Move: Chuyển sang STRING để tránh lỗi ENUM đồng bộ với Database
        type: DataTypes.STRING,
        allowNull: false
      },
      payment_time: {
        type: DataTypes.DATE,
        allowNull: false,
        defaultValue: DataTypes.NOW
      }
    },
    {
      tableName: 'Payments',
      timestamps: false
    }
  );
};
