import { useState } from 'react';
import { Button } from '../button/Button';
import { useToast } from '../toast/useToast';
import { addItem } from '../../store/cartSlice';
import { useAppDispatch } from '../../store/hooks';
import type { AddToCartPayload } from '../../store/cartTypes';
import type { AvailabilityStatus } from '../../hooks/types';
import { shouldShowNotifyMeForProduct } from '../../utils/availabilityStatus';
import { wait } from '../../utils/wait';

interface AddToCartButtonProps extends AddToCartPayload {
  availabilityStatus: AvailabilityStatus;
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
  const { showToast } = useToast();
  const [isAdding, setIsAdding] = useState(false);
  const showNotifyMe = shouldShowNotifyMeForProduct({ availabilityStatus });
  const label = showNotifyMe ? 'Notify Me' : isAdding ? 'Adding...' : 'Add to Cart';

  async function handleAddToCart() {
    // Simulate a real add-to-cart request so the button reflects in-flight state.
    setIsAdding(true);
    await wait(500);

    dispatch(
      addItem({
        sku,
        title,
        price,
        discountPercentage,
        thumbnail,
      })
    );
    showToast('Added to cart!');
    setIsAdding(false);
  }

  return (
    <Button
      variant="primary"
      className="mt-auto"
      disabled={isAdding}
      aria-busy={isAdding}
      onClick={() => {
        if (showNotifyMe) {
          onOpenDetails(productId);
          return;
        }

        void handleAddToCart();
      }}
    >
      {label}
    </Button>
  );
}
