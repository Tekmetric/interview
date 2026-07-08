import { fetchJson } from './apiClient';
import { isProductsResponse } from './guards';
import type { GetSortedProductsParams, ProductsResponse } from './types';

export async function getSortedProducts(
  params: GetSortedProductsParams
): Promise<ProductsResponse> {
  const { sortBy, order, limit = 12, skip = 0 } = params;

  return fetchJson<ProductsResponse>('/products', {
    query: { sortBy, order, limit, skip },
    validate: isProductsResponse,
  });
}
