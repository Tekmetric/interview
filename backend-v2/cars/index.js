const Car = require("./models/Car");
const router = require("./routes");

const getFilters = (queryParams) => {
  const filters = {};
  if (queryParams.brand && queryParams.brand !== "all") {
    filters["brand"] = { $regex: new RegExp(queryParams.brand, "i") };
  }
  if (queryParams.color && queryParams.color !== "all") {
    filters["color"] = { $regex: new RegExp(queryParams.color, "i") };
  }
};

router.get("/", async (req, res) => {
  console.log("Getting cars");
  console.log(req.query);
  const cars = await Car.find(getFilters(req.query));
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
