import { useId, useState } from 'react';
import { formControlClassName } from '../../styles/formControl';
import { Button } from '../button/Button';
import { useToast } from '../toast/useToast';
import { QuantityInput } from '../quantity_input/QuantityInput';
import { addItem } from '../../store/cartSlice';
import { useAppDispatch } from '../../store/hooks';
import type { AddToCartPayload } from '../../store/cartTypes';
import { isValidEmail } from '../../utils/isValidEmail';
import { wait } from '../../utils/wait';

interface ProductDetailsActionProps extends AddToCartPayload {
  inStock: boolean;
  onClose: () => void;
}

export function ProductDetailsAction({
  sku,
  title,
  price,
  discountPercentage,
  thumbnail,
  inStock,
  onClose,
}: ProductDetailsActionProps) {
  const dispatch = useAppDispatch();
  const { showToast } = useToast();
  const [quantity, setQuantity] = useState(1);
  const [email, setEmail] = useState('');
  const [emailError, setEmailError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const emailInputId = useId();
  const emailErrorId = useId();

  // In lieu of a real endpoint, we make up a fascimile.
  // Given a real one, we would have more error/response handling here.
  async function handleNotifyMe() {
    const trimmedEmail = email.trim();

    if (!trimmedEmail || !isValidEmail(trimmedEmail)) {
      setEmailError('Please enter a valid email address.');
      return;
    }

    setEmailError(null);
    setIsSubmitting(true);

    try {
      await wait(500);

      showToast(`Success! ${trimmedEmail} subscribed to stock updates.`);
      onClose();
    } finally {
      setIsSubmitting(false);
    }
  }

  function handleAddToCart() {
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
    showToast('Added to cart!');
    onClose();
  }

  if (!inStock) {
    return (
      <div className="product-details-action product-details-action--notify">
        <div className="product-details-action__email">
          <label className="sr-only" htmlFor={emailInputId}>
            Email for stock updates
          </label>
          <input
            id={emailInputId}
            type="email"
            value={email}
            onChange={(event) => {
              setEmail(event.target.value);
              if (emailError) {
                setEmailError(null);
              }
            }}
            placeholder="Enter email for stock updates..."
            aria-invalid={emailError ? true : undefined}
            aria-describedby={emailError ? emailErrorId : undefined}
            disabled={isSubmitting}
            className={`${formControlClassName} w-full px-3 py-2 text-text disabled:bg-disabled-bg disabled:text-disabled-text`}
          />
          {emailError ? (
            <p id={emailErrorId} role="alert" className="m-0 mt-1 text-xs text-error">
              {emailError}
            </p>
          ) : null}
        </div>
        <Button
          variant="tertiary"
          className="product-details-action__notify-button"
          disabled={isSubmitting}
          onClick={() => {
            void handleNotifyMe();
          }}
        >
          {isSubmitting ? 'Submitting...' : 'Notify Me'}
        </Button>
      </div>
    );
  }

  return (
    <div className="product-details-action">
      <div className="product-details-action__quantity">
        <QuantityInput
          id={`product-qty-${sku}`}
          label={`Quantity for ${title}`}
          value={quantity}
          className="w-full"
          onChange={setQuantity}
        />
      </div>
      <Button
        variant="primary"
        className="product-details-action__button"
        onClick={handleAddToCart}
      >
        Add to Cart
      </Button>
    </div>
  );
}
