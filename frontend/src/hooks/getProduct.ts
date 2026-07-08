import { fetchJson } from './apiClient';
import { isProduct } from './guards';
import type { Product } from './types';

export async function getProduct(id: number): Promise<Product> {
  return fetchJson<Product>(`/products/${id}`, {
    validate: isProduct,
  });
}
