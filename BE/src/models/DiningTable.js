const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
  return sequelize.define(
    'DiningTable',
    {
      table_id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
      },
      table_number: {
        type: DataTypes.INTEGER,
        allowNull: false,
        unique: true
      },
      capacity: {
        type: DataTypes.INTEGER,
        allowNull: false
      },
      status: {
        type: DataTypes.ENUM('Available', 'Occupied'),
        allowNull: false,
        defaultValue: 'Available'
      }
    },
    {
      tableName: 'DiningTables',
      timestamps: false
    }
  );
};
