import axios from 'axios';

export const getReviews = async (page = 1) => {
	const response = await axios.get(`/review?page=${page}`);

	return response.data;
};
