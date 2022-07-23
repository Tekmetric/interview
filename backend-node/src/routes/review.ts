import { Router } from 'express';
import { PAGE_SIZE } from '../config';
import { reviewList } from '../data/reviews';

const router = Router();

router.get('/', (req, res) => {
    const page = (req.query.page || 1) as number;

    if (Number.isNaN(page)) {
        return res.status(400).send({ error: 'Page must be a number' });
    }

    if (page < 1) {
        return res
            .status(400)
            .send({ error: 'Page must be greater than or equal to 1' });
    }

    res.send(reviewList.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE));
});

export default router;
