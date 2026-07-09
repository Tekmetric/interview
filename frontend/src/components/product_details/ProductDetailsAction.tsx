import { Button } from '../button/Button';
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
  const label = inStock ? 'Add to Cart' : 'Notify Me';

  return (
    <Button
      variant="primary"
      className="w-full"
      onClick={() => {
        if (inStock) {
          dispatch(
            addItem({
              sku,
              title,
              price,
              discountPercentage,
              thumbnail,
            })
          );
          return;
        }

        console.log(`${label} clicked`, { sku });
      }}
    >
      {label}
    </Button>
  );
}
