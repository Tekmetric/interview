import { fetchProducts } from './fetchProducts';
import type { GetProductsByCategoryParams, ProductsResponse } from './types';

export async function getProductsByCategory(
  params: GetProductsByCategoryParams
): Promise<ProductsResponse> {
  const { category, ...rest } = params;
  return fetchProducts({ mode: 'category', category, ...rest });
}
