import { fetchJson } from './apiClient';
import { isProductDetailRaw } from './guards';
import { mapProductDetail } from './productMappers';
import { PRODUCT_DETAIL_SELECT } from './productSelectFields';
import type { ProductDetail } from './types';

export async function getProduct(id: number): Promise<ProductDetail> {
  const raw = await fetchJson(`/products/${id}`, {
    query: { select: PRODUCT_DETAIL_SELECT },
    validate: isProductDetailRaw,
  });

  return mapProductDetail(raw);
}
