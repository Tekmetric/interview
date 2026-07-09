import type {
  ProductDetail,
  ProductDetailReview,
  ProductSummary,
  ProductsResponse,
} from './types';

export interface ProductSummaryRaw {
  id: number;
  sku: string;
  title: string;
  brand?: string;
  price: number;
  discountPercentage: number;
  rating: number;
  reviews: unknown[];
  thumbnail: string;
  availabilityStatus: string;
}

interface ProductDetailReviewRaw {
  rating: number;
  comment: string;
  date: string;
  reviewerName: string;
  reviewerEmail?: string;
}

export interface ProductDetailRaw {
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
  dimensions: { width: number; height: number; depth: number };
  shippingInformation: string;
  availabilityStatus: string;
  returnPolicy: string;
  reviews: ProductDetailReviewRaw[];
}

export interface ProductsResponseRaw {
  products: ProductSummaryRaw[];
  total: number;
  skip: number;
  limit: number;
}

export function mapProductSummary(raw: ProductSummaryRaw): ProductSummary {
  return {
    id: raw.id,
    sku: raw.sku,
    title: raw.title,
    ...(raw.brand !== undefined ? { brand: raw.brand } : {}),
    price: raw.price,
    discountPercentage: raw.discountPercentage,
    rating: raw.rating,
    reviewCount: raw.reviews.length,
    thumbnail: raw.thumbnail,
    availabilityStatus: raw.availabilityStatus,
  };
}

export function mapProductsResponse(raw: ProductsResponseRaw): ProductsResponse {
  return {
    products: raw.products.map(mapProductSummary),
    total: raw.total,
    skip: raw.skip,
    limit: raw.limit,
  };
}

function mapProductDetailReview(raw: ProductDetailReviewRaw): ProductDetailReview {
  return {
    rating: raw.rating,
    comment: raw.comment,
    date: raw.date,
    reviewerName: raw.reviewerName,
  };
}

export function mapProductDetail(raw: ProductDetailRaw): ProductDetail {
  return {
    id: raw.id,
    sku: raw.sku,
    title: raw.title,
    description: raw.description,
    ...(raw.brand !== undefined ? { brand: raw.brand } : {}),
    price: raw.price,
    discountPercentage: raw.discountPercentage,
    rating: raw.rating,
    stock: raw.stock,
    images: raw.images,
    thumbnail: raw.thumbnail,
    weight: raw.weight,
    dimensions: raw.dimensions,
    shippingInformation: raw.shippingInformation,
    availabilityStatus: raw.availabilityStatus,
    returnPolicy: raw.returnPolicy,
    reviews: raw.reviews.map(mapProductDetailReview),
  };
}
