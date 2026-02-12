import express from "express";
import products from "./products";

const router = express.Router();

const routers = {
    '/products': products,
}

Object.entries(routers).forEach(([path, routerModule]) => {
    router.use(path, routerModule);
});

module.exports = router;
