const express = require("express");
const cors = require("cors");
const mongoose = require("mongoose");
const router = require("./cars");

const app = express();

async function mongooseConnection() {
  await mongoose.connect("mongodb://127.0.0.1:27017/cars-app");
}

app.use(cors());
app.use(express.json());

app.use("/cars", router);

mongooseConnection();
app.listen(3001, function () {
  console.log("Example app listening on port 3001!");
});
