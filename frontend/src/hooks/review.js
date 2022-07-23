import { useEffect, useState } from 'react';
import { getReviews } from '../api/review';

/**
 * @param {number} page
 * @returns { { reviews: Array<Object>, loading: boolean } }
 */
export const useReviews = page => {
	const [loading, setLoading] = useState(false);
	const [reviews, setReviews] = useState([]);

	useEffect(() => {
		(async () => {
			setLoading(true);

			const list = await getReviews(page);

			// simulate 0.5s wait
			await new Promise(resolve => setTimeout(resolve, 500));

			setReviews(list);
			setLoading(false);
		})();
	}, [page]);

	return {
		reviews,
		loading
	};
};
