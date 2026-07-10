export { getProducts } from './getProducts';
export { getProduct } from './getProduct';
export { getSortedProducts } from './getSortedProducts';
export { searchProducts } from './searchProducts';
export { getCategories } from './getCategories';
export { getProductsByCategory } from './getProductsByCategory';

export type {
  AvailabilityStatus,
  ProductSummary,
  ProductDetail,
  ProductDetailReview,
  ProductDimensions,
  ProductsResponse,
  GetProductsParams,
  GetSortedProductsParams,
  SearchProductsParams,
  ProductCategory,
  GetProductsByCategoryParams,
  SortOption,
  ProductSortField,
  SortOrder,
} from '../../types/product';

export {
  ApiError,
  HttpError,
  TimeoutError,
  NetworkError,
  InvalidResponseError,
} from './errors';
