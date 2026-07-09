import { fetchJson } from './apiClient';
import { isProductsResponseRaw } from './guards';
import { mapProductsResponse } from './productMappers';
import { PRODUCT_SUMMARY_SELECT } from './productSelectFields';
import type { ProductsResponse, SearchProductsParams } from './types';

export async function searchProducts(
  params: SearchProductsParams
): Promise<ProductsResponse> {
  const { q, limit = 12, skip = 0, sortBy, order } = params;

  const raw = await fetchJson('/products/search', {
    query: { q, limit, skip, sortBy, order, select: PRODUCT_SUMMARY_SELECT },
    validate: isProductsResponseRaw,
  });

  return mapProductsResponse(raw);
}
