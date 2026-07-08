export { getProducts } from './getProducts';
export { getProduct } from './getProduct';
export { getSortedProducts } from './getSortedProducts';
export { searchProducts } from './searchProducts';

export type {
  Product,
  ProductsResponse,
  GetProductsParams,
  GetSortedProductsParams,
  SearchProductsParams,
  SortOption,
  ProductSortField,
  SortOrder,
} from './types';

export { SORT_OPTIONS, DEFAULT_SORT_OPTION_ID } from './types';

export {
  ApiError,
  HttpError,
  TimeoutError,
  NetworkError,
  InvalidResponseError,
} from './errors';
