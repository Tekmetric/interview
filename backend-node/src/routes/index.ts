import { Router } from 'express';
import reviewRouter from './review';

const router = Router();

const routes = {
    // Add more routes here
    review: reviewRouter,
};

Object.entries(routes).forEach(([key, value]) => {
    router.use(`/${key}`, value);
});

export default router;
