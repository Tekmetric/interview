const Car = require("./models/Car");
const router = require("./routes");

router.get("/", async (req, res) => {
  const cars = await Car.find();
  res.send(cars).status(200);
});

router.get("/:id", async (req, res) => {
  const car = await Car.findById(req.params.id);
  res.send(car).status(200);
});

router.post("/", async (req, res) => {
  console.log(req.body);
  const car = new Car(req.body);
  await car.save();
  res.send(car).status(201);
});

router.patch("/:id", async (req, res) => {
  const car = await Car.findOneAndUpdate({ _id: req.params.id }, req.body, {
    new: true,
  });
  res.send(car).status(200);
});

router.delete("/:id", async (req, res) => {
  const car = await Car.findOneAndDelete({ _id: req.params.id });
  res.send().status(204);
});

module.exports = router;
