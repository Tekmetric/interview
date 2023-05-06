const Car = require("./models/Car");
const router = require("./routes");

const RECORDS_PER_PAGE = 4;

const getFilters = (queryParams) => {
  const filters = {};
  if (queryParams.brand && queryParams.brand !== "all") {
    filters["brand"] = { $regex: new RegExp(queryParams.brand, "i") };
  }
  if (queryParams.color && queryParams.color !== "all") {
    filters["color"] = { $regex: new RegExp(queryParams.color, "i") };
  }
  return filters;
};

const getPaging = (queryParams) => {
  return {
    limit: RECORDS_PER_PAGE,
    skip: (parseInt(queryParams.page) - 1) * RECORDS_PER_PAGE,
  };
};

const getTotalPages = (totalCars) => {
  if (totalCars % RECORDS_PER_PAGE === 0) {
    return parseInt(totalCars / RECORDS_PER_PAGE);
  }
  return parseInt(totalCars / RECORDS_PER_PAGE) + 1;
};

router.get("/", async (req, res) => {
  console.log("Getting cars");
  console.log(req.query);
  const cars = await Car.find(
    { ...getFilters(req.query) },
    {},
    { ...getPaging(req.query) }
  );
  const totalCars = await Car.count({ ...getFilters(req.query) });
  res.send({ cars, carPages: getTotalPages(totalCars) }).status(200);
});

router.get("/brands", async (req, res) => {
  console.log("Getting brands");
  const brands = await Car.distinct("brand");
  const brandsOptions = brands.map((brand) => ({
    key: brand.toLowerCase(),
    value: brand,
  }));
  brandsOptions.unshift({ key: "all", value: "All" });
  res.send(brandsOptions).status(200);
});

router.get("/colors", async (req, res) => {
  console.log("Getting colors");
  const colors = await Car.distinct("color");
  const colorsOptions = colors.map((color) => ({
    key: color.toLowerCase(),
    value: color,
  }));
  colorsOptions.unshift({ key: "all", value: "All" });
  res.send(colorsOptions).status(200);
});

router.get("/:id", async (req, res) => {
  console.log("Getting car " + req.params.id);
  const car = await Car.findById(req.params.id);
  res.send(car).status(200);
});

router.post("/", async (req, res) => {
  console.log("Creating car " + req.body);
  const car = new Car(req.body);
  await car.save();
  res.send(car).status(201);
});

router.patch("/:id", async (req, res) => {
  console.log("Updating car " + req.params.id);
  console.log(req.body);
  const car = await Car.findOneAndUpdate({ _id: req.params.id }, req.body, {
    new: true,
  });
  res.send(car).status(200);
});

router.delete("/:id", async (req, res) => {
  console.log("Deleting car " + req.params.id);
  const car = await Car.findOneAndDelete({ _id: req.params.id });
  res.send(car).status(204);
});

module.exports = router;
