import { fetchJson } from './apiClient';
import { isProductsResponseRaw } from './guards';
import { mapProductsResponse } from './productMappers';
import { PRODUCT_SUMMARY_SELECT } from './productSelectFields';
import type { GetProductsByCategoryParams, ProductsResponse } from './types';

export async function getProductsByCategory(
  params: GetProductsByCategoryParams
): Promise<ProductsResponse> {
  const { category, limit = 12, skip = 0, sortBy, order } = params;

  const raw = await fetchJson(`/products/category/${category}`, {
    query: { limit, skip, sortBy, order, select: PRODUCT_SUMMARY_SELECT },
    validate: isProductsResponseRaw,
  });

  return mapProductsResponse(raw);
}
