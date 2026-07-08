import { fetchJson } from './apiClient';
import { isProductCategoriesResponse } from './guards';
import type { ProductCategory } from './types';

export async function getCategories(): Promise<ProductCategory[]> {
  return fetchJson<ProductCategory[]>('/products/categories', {
    validate: isProductCategoriesResponse,
  });
}
