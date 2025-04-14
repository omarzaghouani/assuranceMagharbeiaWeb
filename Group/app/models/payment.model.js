module.exports = (sequelize, DataTypes) => {
    const Payment = sequelize.define("payment", {
      amount: {
        type: DataTypes.FLOAT
      },
      method: {
        type: DataTypes.STRING
      },
      status: {
        type: DataTypes.STRING
      },
      date: {
        type: DataTypes.DATE
      }
    });
  
    return Payment;
  };
  