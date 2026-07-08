export { getProducts } from './getProducts';
export { getProduct } from './getProduct';
export { searchProducts } from './searchProducts';

export type {
  Product,
  ProductsResponse,
  GetProductsParams,
  SearchProductsParams,
} from './types';

export {
  ApiError,
  HttpError,
  TimeoutError,
  NetworkError,
  InvalidResponseError,
} from './errors';
