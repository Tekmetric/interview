import { useEffect, useState } from 'react';
import { getReviews } from '../api/review';

/**
 * @param {number} page
 * @returns { { reviews: Array<Object> } }
 */
export const useReviews = page => {
	const [reviews, setReviews] = useState([]);

	useEffect(() => {
		(async () => {
			const list = await getReviews(page);

			setReviews(list);
		})();
	}, [page]);

	return {
		reviews
	};
};
