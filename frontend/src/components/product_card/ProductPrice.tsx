import { formatProductPrice } from './priceUtils';

interface ProductPriceProps {
  price: number;
  discountPercentage: number;
}

export function ProductPrice({ price, discountPercentage }: ProductPriceProps) {
  const { display, discountLabel } = formatProductPrice(
    price,
    discountPercentage
  );

  return (
    <p className="product-card__price">
      {display}
      {discountLabel && (
        <span className="product-card__discount"> ({discountLabel})</span>
      )}
    </p>
  );
}
