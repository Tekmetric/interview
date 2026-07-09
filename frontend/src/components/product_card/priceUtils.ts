export interface FormattedProductPrice {
  display: string;
  discountLabel: string | null;
}

const usdFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

export function formatCurrency(amount: number): string {
  return usdFormatter.format(amount);
}

export function formatProductPrice(
  price: number,
  discountPercentage: number
): FormattedProductPrice {
  const display = usdFormatter.format(price);
  const discountLabel =
    discountPercentage > 0
      ? `${Math.round(discountPercentage)}% off`
      : null;

  return { display, discountLabel };
}
