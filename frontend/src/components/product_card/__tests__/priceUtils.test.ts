import { formatProductPrice } from '../priceUtils';

describe('formatProductPrice', () => {
  it('formats price without discount when discount is 0', () => {
    expect(formatProductPrice(9.99, 0)).toEqual({
      display: '$9.99',
      discountLabel: null,
    });
  });

  it('formats price with rounded discount label', () => {
    expect(formatProductPrice(9.99, 7.17)).toEqual({
      display: '$9.99',
      discountLabel: '7% off',
    });
  });

  it('formats whole numbers with two decimal places', () => {
    expect(formatProductPrice(10, 0)).toEqual({
      display: '$10.00',
      discountLabel: null,
    });
  });
});
