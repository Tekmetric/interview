import type { ProductCategory } from './types';
import type { ProductDetailRaw, ProductsResponseRaw } from './productMappers';

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}

function isStringArray(value: unknown): value is string[] {
  return Array.isArray(value) && value.every((item) => typeof item === 'string');
}

function isProductDimensions(value: unknown): boolean {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.width === 'number' &&
    typeof value.height === 'number' &&
    typeof value.depth === 'number'
  );
}

function isProductSummaryReview(value: unknown): boolean {
  return isRecord(value);
}

function isProductDetailReview(value: unknown): boolean {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.rating === 'number' &&
    typeof value.comment === 'string' &&
    typeof value.date === 'string' &&
    typeof value.reviewerName === 'string'
  );
}

export function isProductSummaryRaw(value: unknown): boolean {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.id === 'number' &&
    typeof value.sku === 'string' &&
    typeof value.title === 'string' &&
    (value.brand === undefined || typeof value.brand === 'string') &&
    typeof value.price === 'number' &&
    typeof value.discountPercentage === 'number' &&
    typeof value.rating === 'number' &&
    Array.isArray(value.reviews) &&
    value.reviews.every(isProductSummaryReview) &&
    typeof value.thumbnail === 'string' &&
    typeof value.availabilityStatus === 'string'
  );
}

export function isProductDetailRaw(value: unknown): value is ProductDetailRaw {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.id === 'number' &&
    typeof value.sku === 'string' &&
    typeof value.title === 'string' &&
    typeof value.description === 'string' &&
    (value.brand === undefined || typeof value.brand === 'string') &&
    typeof value.price === 'number' &&
    typeof value.discountPercentage === 'number' &&
    typeof value.rating === 'number' &&
    typeof value.stock === 'number' &&
    isStringArray(value.images) &&
    typeof value.thumbnail === 'string' &&
    typeof value.weight === 'number' &&
    isProductDimensions(value.dimensions) &&
    typeof value.shippingInformation === 'string' &&
    typeof value.availabilityStatus === 'string' &&
    typeof value.returnPolicy === 'string' &&
    Array.isArray(value.reviews) &&
    value.reviews.every(isProductDetailReview)
  );
}

export function isProductsResponseRaw(
  value: unknown
): value is ProductsResponseRaw {
  if (!isRecord(value)) {
    return false;
  }

  return (
    Array.isArray(value.products) &&
    value.products.every(isProductSummaryRaw) &&
    typeof value.total === 'number' &&
    typeof value.skip === 'number' &&
    typeof value.limit === 'number'
  );
}

export function isProductCategory(value: unknown): value is ProductCategory {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.slug === 'string' &&
    typeof value.name === 'string' &&
    typeof value.url === 'string'
  );
}

export function isProductCategoriesResponse(
  value: unknown
): value is ProductCategory[] {
  return Array.isArray(value) && value.every(isProductCategory);
}
