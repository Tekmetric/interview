import type { ProductDetail } from '../../types/product';
import { fetchJson } from './client';
import { isProductDetailRaw } from './guards';
import { mapProductDetail } from './mappers';
import { PRODUCT_DETAIL_SELECT } from './selectFields';

export async function getProduct(id: number): Promise<ProductDetail> {
  const raw = await fetchJson(`/products/${id}`, {
    query: { select: PRODUCT_DETAIL_SELECT },
    validate: isProductDetailRaw,
  });

  return mapProductDetail(raw);
}
