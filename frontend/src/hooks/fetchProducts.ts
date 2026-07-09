import { fetchJson } from './apiClient';
import { DEFAULT_PAGE_SIZE } from '../constants/pagination';
import { isProductsResponseRaw } from './guards';
import { mapProductsResponse } from './productMappers';
import { PRODUCT_SUMMARY_SELECT } from './productSelectFields';
import type {
  ProductSortField,
  ProductsResponse,
  SortOrder,
} from './types';

type PaginationParams = {
  limit?: number;
  skip?: number;
};

type SortParams = {
  sortBy?: ProductSortField;
  order?: SortOrder;
};

export type FetchProductsParams =
  | ({ mode: 'default' } & PaginationParams)
  | ({ mode: 'search'; q: string } & PaginationParams & SortParams)
  | ({ mode: 'category'; category: string } & PaginationParams & SortParams)
  | ({
      mode: 'sorted';
      sortBy: ProductSortField;
      order: SortOrder;
    } & PaginationParams);

export async function fetchProducts(
  params: FetchProductsParams
): Promise<ProductsResponse> {
  const limit = params.limit ?? DEFAULT_PAGE_SIZE;
  const skip = params.skip ?? 0;

  switch (params.mode) {
    case 'search': {
      const raw = await fetchJson('/products/search', {
        query: {
          q: params.q,
          limit,
          skip,
          sortBy: params.sortBy,
          order: params.order,
          select: PRODUCT_SUMMARY_SELECT,
        },
        validate: isProductsResponseRaw,
      });
      return mapProductsResponse(raw);
    }
    case 'category': {
      const raw = await fetchJson(`/products/category/${params.category}`, {
        query: {
          limit,
          skip,
          sortBy: params.sortBy,
          order: params.order,
          select: PRODUCT_SUMMARY_SELECT,
        },
        validate: isProductsResponseRaw,
      });
      return mapProductsResponse(raw);
    }
    case 'sorted': {
      const raw = await fetchJson('/products', {
        query: {
          sortBy: params.sortBy,
          order: params.order,
          limit,
          skip,
          select: PRODUCT_SUMMARY_SELECT,
        },
        validate: isProductsResponseRaw,
      });
      return mapProductsResponse(raw);
    }
    case 'default': {
      const raw = await fetchJson('/products', {
        query: { limit, skip, select: PRODUCT_SUMMARY_SELECT },
        validate: isProductsResponseRaw,
      });
      return mapProductsResponse(raw);
    }
  }
}
