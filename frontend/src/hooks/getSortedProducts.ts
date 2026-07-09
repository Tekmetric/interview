import { fetchJson } from './apiClient';
import { isProductsResponseRaw } from './guards';
import { mapProductsResponse } from './productMappers';
import { PRODUCT_SUMMARY_SELECT } from './productSelectFields';
import type { GetSortedProductsParams, ProductsResponse } from './types';

export async function getSortedProducts(
  params: GetSortedProductsParams
): Promise<ProductsResponse> {
  const { sortBy, order, limit = 12, skip = 0 } = params;

  const raw = await fetchJson('/products', {
    query: { sortBy, order, limit, skip, select: PRODUCT_SUMMARY_SELECT },
    validate: isProductsResponseRaw,
  });

  return mapProductsResponse(raw);
}
