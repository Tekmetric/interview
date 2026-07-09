import { fetchJson } from './apiClient';
import { isProductsResponseRaw } from './guards';
import { mapProductsResponse } from './productMappers';
import { PRODUCT_SUMMARY_SELECT } from './productSelectFields';
import type { GetProductsParams, ProductsResponse } from './types';

export async function getProducts(
  params: GetProductsParams = {}
): Promise<ProductsResponse> {
  const { limit = 12, skip = 0 } = params;

  const raw = await fetchJson('/products', {
    query: { limit, skip, select: PRODUCT_SUMMARY_SELECT },
    validate: isProductsResponseRaw,
  });

  return mapProductsResponse(raw);
}
