
import { Router } from "express";
import { NR_OF_ITEMS_PER_PAGE } from "../configs";
import { products } from "../data/products";

const router = Router();

router.get("/products", async (req, res) => {
    const page = req.query.page || 1;
    console.log(page);

    if (isNaN(Number(page)) || Number(page) < 1) {
        return res.status(400).send("Invalid page number");
    }

    const skip = (Number(page) - 1) * NR_OF_ITEMS_PER_PAGE;
    const limit = NR_OF_ITEMS_PER_PAGE;
    // Simulate network delay
    await new Promise((resolve) => setTimeout(resolve, 500));

    return res.json({
        data: products.slice(skip, skip + limit),
        total: products.length,
        page: Number(page),
        pages: Math.ceil(products.length / NR_OF_ITEMS_PER_PAGE),
    });
});

export default router;
