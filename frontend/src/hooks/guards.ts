import type { Product, ProductsResponse } from './types';

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

function isProductReview(value: unknown): boolean {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.rating === 'number' &&
    typeof value.comment === 'string' &&
    typeof value.date === 'string' &&
    typeof value.reviewerName === 'string' &&
    typeof value.reviewerEmail === 'string'
  );
}

function isProductMeta(value: unknown): boolean {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.createdAt === 'string' &&
    typeof value.updatedAt === 'string' &&
    typeof value.barcode === 'string' &&
    typeof value.qrCode === 'string'
  );
}

export function isProduct(value: unknown): value is Product {
  if (!isRecord(value)) {
    return false;
  }

  return (
    typeof value.id === 'number' &&
    typeof value.title === 'string' &&
    typeof value.description === 'string' &&
    typeof value.category === 'string' &&
    typeof value.price === 'number' &&
    typeof value.discountPercentage === 'number' &&
    typeof value.rating === 'number' &&
    typeof value.stock === 'number' &&
    isStringArray(value.tags) &&
    typeof value.brand === 'string' &&
    typeof value.sku === 'string' &&
    typeof value.weight === 'number' &&
    isProductDimensions(value.dimensions) &&
    typeof value.warrantyInformation === 'string' &&
    typeof value.shippingInformation === 'string' &&
    typeof value.availabilityStatus === 'string' &&
    Array.isArray(value.reviews) &&
    value.reviews.every(isProductReview) &&
    typeof value.returnPolicy === 'string' &&
    typeof value.minimumOrderQuantity === 'number' &&
    isProductMeta(value.meta) &&
    typeof value.thumbnail === 'string' &&
    isStringArray(value.images)
  );
}

export function isProductsResponse(value: unknown): value is ProductsResponse {
  if (!isRecord(value)) {
    return false;
  }

  return (
    Array.isArray(value.products) &&
    value.products.every(isProduct) &&
    typeof value.total === 'number' &&
    typeof value.skip === 'number' &&
    typeof value.limit === 'number'
  );
}

export function isProductArrayResponse(value: unknown): value is Product[] {
  return Array.isArray(value) && value.every(isProduct);
}
