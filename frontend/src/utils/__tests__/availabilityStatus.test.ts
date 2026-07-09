import {
  getAvailabilityBadgeLabel,
  isLowStock,
  isOutOfStock,
  isProductInStock,
  shouldShowNotifyMe,
  shouldShowNotifyMeForProduct,
} from '../availabilityStatus';

describe('availabilityStatus', () => {
  it('identifies out of stock status', () => {
    expect(isOutOfStock('Out of Stock')).toBe(true);
    expect(isOutOfStock('Low Stock')).toBe(false);
    expect(isOutOfStock('In Stock')).toBe(false);
  });

  it('identifies low stock status', () => {
    expect(isLowStock('Low Stock')).toBe(true);
    expect(isLowStock('Out of Stock')).toBe(false);
    expect(isLowStock('In Stock')).toBe(false);
  });

  it('returns badge labels for low and out of stock', () => {
    expect(getAvailabilityBadgeLabel('Out of Stock')).toBe('Out Of Stock');
    expect(getAvailabilityBadgeLabel('Low Stock')).toBe('Low Stock');
    expect(getAvailabilityBadgeLabel('In Stock')).toBeNull();
  });

  it('shows notify me only for out of stock', () => {
    expect(shouldShowNotifyMe('Out of Stock')).toBe(true);
    expect(shouldShowNotifyMe('Low Stock')).toBe(false);
    expect(shouldShowNotifyMe('In Stock')).toBe(false);
  });

  it('unifies stock signals for purchase state', () => {
    expect(
      isProductInStock({ availabilityStatus: 'In Stock', stock: 5 })
    ).toBe(true);
    expect(
      isProductInStock({ availabilityStatus: 'Out of Stock', stock: 5 })
    ).toBe(false);
    expect(
      isProductInStock({ availabilityStatus: 'In Stock', stock: 0 })
    ).toBe(false);
    expect(
      shouldShowNotifyMeForProduct({ availabilityStatus: 'Out of Stock' })
    ).toBe(true);
  });
});
