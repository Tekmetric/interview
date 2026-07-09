import type { AvailabilityStatus } from '../hooks/types';

const OUT_OF_STOCK = 'Out of Stock';
const LOW_STOCK = 'Low Stock';

export function isOutOfStock(status: AvailabilityStatus): boolean {
  return status === OUT_OF_STOCK;
}

export function isLowStock(status: AvailabilityStatus): boolean {
  return status === LOW_STOCK;
}

export function getAvailabilityBadgeLabel(
  status: AvailabilityStatus
): 'Out Of Stock' | 'Low Stock' | null {
  if (isOutOfStock(status)) {
    return 'Out Of Stock';
  }

  if (isLowStock(status)) {
    return 'Low Stock';
  }

  return null;
}

export function shouldShowNotifyMe(status: AvailabilityStatus): boolean {
  return isOutOfStock(status);
}

export interface ProductPurchaseSignals {
  availabilityStatus: AvailabilityStatus;
  stock?: number;
}

export function isProductInStock(product: ProductPurchaseSignals): boolean {
  if (isOutOfStock(product.availabilityStatus)) {
    return false;
  }

  if (product.stock !== undefined && product.stock <= 0) {
    return false;
  }

  return true;
}

export function shouldShowNotifyMeForProduct(
  product: ProductPurchaseSignals
): boolean {
  return !isProductInStock(product);
}
