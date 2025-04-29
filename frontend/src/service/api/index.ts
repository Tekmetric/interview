import axios, { type AxiosError, HttpStatusCode } from 'axios';
import axiosRetry, { isNetworkOrIdempotentRequestError } from 'axios-retry';

const api = axios.create({
	headers: {
		'Content-Type': 'application/json',
	},
});

axiosRetry(api, {
	retries: 3,
	retryDelay: (retryCount: number) => retryCount * 1000,
	retryCondition: (error: AxiosError) =>
		isNetworkOrIdempotentRequestError(error) ||
		error?.response?.status === HttpStatusCode.InternalServerError,
});

export default api;
