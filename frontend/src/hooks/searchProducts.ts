import { fetchProducts } from './fetchProducts';
import type { ProductsResponse, SearchProductsParams } from './types';

export async function searchProducts(
  params: SearchProductsParams
): Promise<ProductsResponse> {
  const { q, ...rest } = params;
  return fetchProducts({ mode: 'search', q, ...rest });
}
