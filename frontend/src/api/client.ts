import axios from 'axios';

const baseURL = 'https://fakestoreapi.com/products';

export const apiClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => Promise.reject(error instanceof Error ? error : new Error(error.message || 'An error occurred')),
);
