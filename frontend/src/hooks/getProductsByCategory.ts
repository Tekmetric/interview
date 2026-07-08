import { fetchJson } from './apiClient';
import { isProductsResponse } from './guards';
import type { GetProductsByCategoryParams, ProductsResponse } from './types';

export async function getProductsByCategory(
  params: GetProductsByCategoryParams
): Promise<ProductsResponse> {
  const { category, limit = 12, skip = 0, sortBy, order } = params;

  return fetchJson<ProductsResponse>(`/products/category/${category}`, {
    query: { limit, skip, sortBy, order },
    validate: isProductsResponse,
  });
}
