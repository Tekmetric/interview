import { fetchProducts } from './fetchProducts';
import type { GetSortedProductsParams, ProductsResponse } from './types';

export async function getSortedProducts(
  params: GetSortedProductsParams
): Promise<ProductsResponse> {
  const { sortBy, order, ...rest } = params;
  return fetchProducts({ mode: 'sorted', sortBy, order, ...rest });
}
