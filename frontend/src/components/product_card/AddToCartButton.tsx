import { Button } from '../button/Button';
import { addItem } from '../../store/cartSlice';
import { useAppDispatch } from '../../store/hooks';
import type { AddToCartPayload } from '../../store/cartTypes';

type AddToCartButtonProps = AddToCartPayload;

export function AddToCartButton({
  sku,
  title,
  price,
  discountPercentage,
  thumbnail,
}: AddToCartButtonProps) {
  const dispatch = useAppDispatch();

  return (
    <Button
      variant="primary"
      className="mt-auto"
      onClick={() =>
        dispatch(
          addItem({
            sku,
            title,
            price,
            discountPercentage,
            thumbnail,
          })
        )
      }
    >
      Add to Cart
    </Button>
  );
}
