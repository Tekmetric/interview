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

/** Fields returned by list/search endpoints for product cards. */
export interface ProductSummary {
  id: number;
  sku: string;
  title: string;
  brand?: string;
  price: number;
  discountPercentage: number;
  rating: number;
  reviewCount: number;
  thumbnail: string;
}

/** Fields returned by the single-product endpoint for the details drawer. */
export interface ProductDetailReview {
  rating: number;
  comment: string;
  date: string;
  reviewerName: string;
}

export interface ProductDetail {
  id: number;
  sku: string;
  title: string;
  description: string;
  brand?: string;
  price: number;
  discountPercentage: number;
  rating: number;
  stock: number;
  images: string[];
  thumbnail: string;
  weight: number;
  dimensions: ProductDimensions;
  shippingInformation: string;
  availabilityStatus: string;
  returnPolicy: string;
  reviews: ProductDetailReview[];
}

export interface ProductsResponse {
  products: ProductSummary[];
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

export interface ProductCategory {
  slug: string;
  name: string;
  url: string;
}

export interface GetProductsByCategoryParams {
  category: string;
  limit?: number;
  skip?: number;
  sortBy?: ProductSortField;
  order?: SortOrder;
}

export interface SortOption {
  id: string;
  label: string;
  sortBy?: ProductSortField;
  order?: SortOrder;
}
