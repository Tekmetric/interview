const express = require("express");
const app = express();
const mongoose = require("mongoose");

async function mongooseConnection() {
  await mongoose.connect("mongodb://127.0.0.1:27017/cars-app");
}

app.use(express.json());

const router = require("./cars");
app.use("/cars", router);

mongooseConnection();
app.listen(3001, function () {
  console.log("Example app listening on port 3001!");
});
