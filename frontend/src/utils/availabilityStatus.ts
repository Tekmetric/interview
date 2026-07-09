const OUT_OF_STOCK = 'Out of Stock';
const LOW_STOCK = 'Low Stock';

export function isOutOfStock(status: string): boolean {
  return status === OUT_OF_STOCK;
}

export function isLowStock(status: string): boolean {
  return status === LOW_STOCK;
}

export function getAvailabilityBadgeLabel(
  status: string
): 'Out Of Stock' | 'Low Stock' | null {
  if (isOutOfStock(status)) {
    return 'Out Of Stock';
  }

  if (isLowStock(status)) {
    return 'Low Stock';
  }

  return null;
}

export function shouldShowNotifyMe(status: string): boolean {
  return isOutOfStock(status);
}
