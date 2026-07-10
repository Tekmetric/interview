import type { ProductCategory } from '../../types/product';
import { fetchJson } from './client';
import { isProductCategoriesResponse } from './guards';

export async function getCategories(): Promise<ProductCategory[]> {
  return fetchJson<ProductCategory[]>('/products/categories', {
    validate: isProductCategoriesResponse,
  });
}
