import { useState } from 'react';
import { Button } from '../button/Button';
import { QuantityInput } from '../quantity_input/QuantityInput';
import { addItem } from '../../store/cartSlice';
import { useAppDispatch } from '../../store/hooks';
import type { AddToCartPayload } from '../../store/cartTypes';

interface ProductDetailsActionProps extends AddToCartPayload {
  inStock: boolean;
}

export function ProductDetailsAction({
  sku,
  title,
  price,
  discountPercentage,
  thumbnail,
  inStock,
}: ProductDetailsActionProps) {
  const dispatch = useAppDispatch();
  const [quantity, setQuantity] = useState(1);
  const label = inStock ? 'Add to Cart' : 'Notify Me';

  return (
    <div className="product-details-action">
      {inStock ? (
        <div className="product-details-action__quantity">
          <QuantityInput
            id={`product-qty-${sku}`}
            label={`Quantity for ${title}`}
            value={quantity}
            className="w-full"
            onChange={setQuantity}
          />
        </div>
      ) : null}
      <Button
        variant="primary"
        className={inStock ? 'product-details-action__button' : 'w-full'}
        onClick={() => {
          if (inStock) {
            dispatch(
              addItem({
                sku,
                title,
                price,
                discountPercentage,
                thumbnail,
                quantity: Math.max(1, quantity),
              })
            );
            return;
          }

          console.log(`${label} clicked`, { sku });
        }}
      >
        {label}
      </Button>
    </div>
  );
}
