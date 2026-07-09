import { fetchProducts } from './fetchProducts';
import type { GetProductsParams, ProductsResponse } from './types';

export async function getProducts(
  params: GetProductsParams = {}
): Promise<ProductsResponse> {
  return fetchProducts({ mode: 'default', ...params });
}
