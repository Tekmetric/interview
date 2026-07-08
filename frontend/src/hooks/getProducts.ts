import { fetchJson } from './apiClient';
import { isProductsResponse } from './guards';
import type { GetProductsParams, ProductsResponse } from './types';

export async function getProducts(
  params: GetProductsParams = {}
): Promise<ProductsResponse> {
  // arbitrary default values
  const { limit = 12, skip = 0 } = params;

  return fetchJson<ProductsResponse>('/products', {
    query: { limit, skip },
    validate: isProductsResponse,
  });
}
