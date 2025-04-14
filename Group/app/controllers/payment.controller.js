const db = require("../models");
const Payment = db.payments;

exports.create = async (req, res) => {
  try {
    const data = await Payment.create(req.body);
    res.status(201).json(data);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

exports.findAll = async (req, res) => {
  const data = await Payment.findAll();
  res.json(data);
};

exports.findOne = async (req, res) => {
  const data = await Payment.findByPk(req.params.id);
  if (data) res.json(data);
  else res.status(404).send("Not found");
};

exports.update = async (req, res) => {
  await Payment.update(req.body, { where: { id: req.params.id } });
  res.sendStatus(204);
};

exports.delete = async (req, res) => {
  await Payment.destroy({ where: { id: req.params.id } });
  res.sendStatus(204);
};
