import { AxiosResponse } from 'axios';
import { apiClient } from './client';
import { Categories, Product } from './service.types';

/**
 * Fetches all the products available in the store.
 *
 * @returns {Promise} The promise object containing all the products.
 */
export const fetchProducts = (): Promise<AxiosResponse<Product[]>> => apiClient.get('/');

/**
 * Fetches all the categories available in the store.
 *
 * @returns {Promise} The promise object containing the categories.
 */
export const fetchCategories = (): Promise<AxiosResponse<Categories[]>> => apiClient.get('/categories');

/**
 * Fetches the products of a specific category.
 *
 * @param {string} category - The category to fetch the products from.
 * @returns {Promise} The promise object containing the products for the specified category.
 */
export const fetchCategoryProducts = (category: string): Promise<AxiosResponse<Product[]>> =>
  apiClient.get(`/category/${category}`);
