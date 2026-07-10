import { fetchProducts } from './fetchProducts';
import type { GetProductsByCategoryParams, ProductsResponse } from '../../types/product';

export async function getProductsByCategory(
  params: GetProductsByCategoryParams
): Promise<ProductsResponse> {
  const { category, ...rest } = params;
  return fetchProducts({ mode: 'category', category, ...rest });
}
