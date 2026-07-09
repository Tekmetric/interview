import { useId, useState } from 'react';
import { Button } from '../button/Button';
import { useToast } from '../toast/useToast';
import { QuantityInput } from '../quantity_input/QuantityInput';
import { addItem } from '../../store/cartSlice';
import { useAppDispatch } from '../../store/hooks';
import type { AddToCartPayload } from '../../store/cartTypes';
import { isValidEmail } from '../../utils/isValidEmail';

interface ProductDetailsActionProps extends AddToCartPayload {
  inStock: boolean;
  onNotifySuccess: () => void;
}

function wait(ms: number): Promise<void> {
  return new Promise((resolve) => {
    window.setTimeout(resolve, ms);
  });
}

export function ProductDetailsAction({
  sku,
  title,
  price,
  discountPercentage,
  thumbnail,
  inStock,
  onNotifySuccess,
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

    await wait(500);

    showToast(`Success! ${trimmedEmail} subscribed to stock updates.`);
    onNotifySuccess();
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
            className="w-full rounded border border-neutral-300 px-3 py-2 text-sm focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600 disabled:bg-neutral-100 disabled:text-neutral-500"
          />
          {emailError ? (
            <p id={emailErrorId} role="alert" className="m-0 mt-1 text-xs text-red-600">
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
        onClick={() => {
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
        }}
      >
        Add to Cart
      </Button>
    </div>
  );
}
