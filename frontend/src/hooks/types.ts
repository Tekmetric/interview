/**
 * Convenience types mirroring DummyJSON's product payload shape.
 * These are NOT a formal API contract — in a real app, types would be
 * established jointly between backend and frontend. We borrow DummyJSON's
 * backend here for prototyping only.
 */

export interface ProductDimensions {
  width: number;
  height: number;
  depth: number;
}

export interface ProductReview {
  rating: number;
  comment: string;
  date: string;
  reviewerName: string;
  reviewerEmail: string;
}

export interface ProductMeta {
  createdAt: string;
  updatedAt: string;
  barcode: string;
  qrCode: string;
}

export interface Product {
  id: number;
  title: string;
  description: string;
  category: string;
  price: number;
  discountPercentage: number;
  rating: number;
  stock: number;
  tags: string[];
  brand: string;
  sku: string;
  weight: number;
  dimensions: ProductDimensions;
  warrantyInformation: string;
  shippingInformation: string;
  availabilityStatus: string;
  reviews: ProductReview[];
  returnPolicy: string;
  minimumOrderQuantity: number;
  meta: ProductMeta;
  thumbnail: string;
  images: string[];
}

export interface ProductsResponse {
  products: Product[];
  total: number;
  skip: number;
  limit: number;
}

export interface GetProductsParams {
  limit?: number;
  skip?: number;
}

export interface SearchProductsParams {
  q: string;
  limit?: number;
  skip?: number;
  sortBy?: ProductSortField;
  order?: SortOrder;
}

export type ProductSortField = 'price' | 'discountPercentage' | 'rating';

export type SortOrder = 'asc' | 'desc';

export interface GetSortedProductsParams {
  sortBy: ProductSortField;
  order: SortOrder;
  limit?: number;
  skip?: number;
}

export interface SortOption {
  id: string;
  label: string;
  sortBy?: ProductSortField;
  order?: SortOrder;
}

export const DEFAULT_SORT_OPTION_ID = 'default';

export const SORT_OPTIONS: SortOption[] = [
  { id: DEFAULT_SORT_OPTION_ID, label: 'Default' },
  {
    id: 'price-asc',
    label: 'Price (Low to High)',
    sortBy: 'price',
    order: 'asc',
  },
  {
    id: 'price-desc',
    label: 'Price (High to Low)',
    sortBy: 'price',
    order: 'desc',
  },
  {
    id: 'deal',
    label: 'Best Deal',
    sortBy: 'discountPercentage',
    order: 'desc',
  },
  {
    id: 'rating',
    label: 'Best Rated',
    sortBy: 'rating',
    order: 'desc',
  },
];
