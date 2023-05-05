const mongoose = require("mongoose");

const CarSchema = new mongoose.Schema({
  brand: { type: String, required: true },
  model: { type: String, required: true },
  year: Number,
  engineCapacity: Number,
  description: String,
  url: String,
  color: String,
  minPrice: Number,
  maxPrice: Number,
});

const Car = mongoose.model("Car", CarSchema);

module.exports = Car;
