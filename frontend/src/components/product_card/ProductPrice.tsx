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
    <p className="m-0 text-[0.95rem]">
      {display}
      {discountLabel && (
        <span className="text-neutral-500"> ({discountLabel})</span>
      )}
    </p>
  );
}
