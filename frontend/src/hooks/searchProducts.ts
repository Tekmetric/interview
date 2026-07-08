import { fetchJson } from './apiClient';
import { isProductsResponse } from './guards';
import type { ProductsResponse, SearchProductsParams } from './types';

export async function searchProducts(
  params: SearchProductsParams
): Promise<ProductsResponse> {
  const { q, limit = 12, skip = 0 } = params;

  return fetchJson<ProductsResponse>('/products/search', {
    query: { q, limit, skip },
    validate: isProductsResponse,
  });
}
