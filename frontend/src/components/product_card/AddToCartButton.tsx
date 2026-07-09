import { Button } from '../button/Button';
import { addItem } from '../../store/cartSlice';
import { useAppDispatch } from '../../store/hooks';
import type { AddToCartPayload } from '../../store/cartTypes';
import { shouldShowNotifyMe } from '../../utils/availabilityStatus';

interface AddToCartButtonProps extends AddToCartPayload {
  availabilityStatus: string;
  productId: number;
  onOpenDetails: (productId: number) => void;
}

export function AddToCartButton({
  sku,
  title,
  price,
  discountPercentage,
  thumbnail,
  availabilityStatus,
  productId,
  onOpenDetails,
}: AddToCartButtonProps) {
  const dispatch = useAppDispatch();
  const showNotifyMe = shouldShowNotifyMe(availabilityStatus);
  const label = showNotifyMe ? 'Notify Me' : 'Add to Cart';

  return (
    <Button
      variant="primary"
      className="mt-auto"
      onClick={() => {
        if (showNotifyMe) {
          onOpenDetails(productId);
          return;
        }

        dispatch(
          addItem({
            sku,
            title,
            price,
            discountPercentage,
            thumbnail,
          })
        );
      }}
    >
      {label}
    </Button>
  );
}
