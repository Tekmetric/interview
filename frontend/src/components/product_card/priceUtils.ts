export interface FormattedProductPrice {
  display: string;
  discountLabel: string | null;
}

export function formatProductPrice(
  price: number,
  discountPercentage: number
): FormattedProductPrice {
  const display = `$${price.toFixed(2)}`;
  const discountLabel =
    discountPercentage > 0
      ? `${Math.round(discountPercentage)}% off`
      : null;

  return { display, discountLabel };
}
