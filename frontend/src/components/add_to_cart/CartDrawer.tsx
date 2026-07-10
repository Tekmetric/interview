import { useState } from 'react';
import { Button } from '../button/Button';
import { Drawer } from '../drawer/Drawer';
import { QuantityInput } from '../quantity_input/QuantityInput';
import { useToast } from '../toast/useToast';
import { formatCurrency, formatProductPrice } from '../../utils/priceUtils';
import { wait } from '../../utils/wait';
import { removeItem, updateQuantity } from '../../store/cartSlice';
import {
  selectCartItemCount,
  selectCartItems,
  selectCartSubtotal,
} from '../../store/cartSelectors';
import { useAppDispatch, useAppSelector } from '../../store/hooks';
import type { CartItem } from '../../store/cartTypes';

interface CartDrawerProps {
  isOpen: boolean;
  onClose: () => void;
}

export const CART_DRAWER_PANEL_ID = 'cart-drawer-panel';
const CART_CHECKOUT_TOOLTIP_ID = 'cart-checkout-tooltip';
const CHECKOUT_DISABLED_MESSAGE = 'Checkout is not implemented';

export function CartDrawer({ isOpen, onClose }: CartDrawerProps) {
  const dispatch = useAppDispatch();
  const { showToast } = useToast();
  const items = useAppSelector(selectCartItems);
  const itemCount = useAppSelector(selectCartItemCount);
  const subtotal = useAppSelector(selectCartSubtotal);
  const [removingSku, setRemovingSku] = useState<string | null>(null);
  const isEmpty = items.length === 0;

  async function handleRemoveItem(item: CartItem) {
    setRemovingSku(item.sku);
    await wait(500);

    dispatch(removeItem(item.sku));
    showToast('Item removed from cart');
    setRemovingSku(null);
  }

  return (
    <Drawer
      isOpen={isOpen}
      onClose={onClose}
      title="Cart"
      titleId="cart-drawer-title"
      panelId={CART_DRAWER_PANEL_ID}
      closeAriaLabel="Close cart"
      panelClassName="drawer-panel--full-height"
      footer={
        isEmpty ? undefined : (
          <span
            className="group relative block w-full"
            tabIndex={0}
            title={CHECKOUT_DISABLED_MESSAGE}
            aria-describedby={CART_CHECKOUT_TOOLTIP_ID}
          >
            <Button
              variant="primary"
              disabled
              tabIndex={-1}
              className="w-full"
            >
              Proceed to Checkout
            </Button>
            <span
              id={CART_CHECKOUT_TOOLTIP_ID}
              role="tooltip"
              className="pointer-events-none absolute bottom-full left-1/2 z-10 mb-2 hidden w-max max-w-[min(16rem,calc(100vw-2rem))] -translate-x-1/2 rounded border border-border bg-elevated px-3 py-2 text-center text-xs text-text-secondary group-hover:block group-focus-within:block"
            >
              {CHECKOUT_DISABLED_MESSAGE}
            </span>
          </span>
        )
      }
    >
      {isEmpty ? (
        <p className="m-0 text-base text-text-secondary">Nothing to see here. Add some items to your cart!</p>
      ) : (
        <div className="flex flex-col gap-4">
          <ul className="m-0 flex list-none flex-col gap-4 p-0">
            {items.map((item: CartItem) => {
              const { display } = formatProductPrice(
                item.price,
                item.discountPercentage
              );
              const isRemoving = removingSku === item.sku;

              return (
                <li
                  key={item.sku}
                  className="flex gap-3 border-b border-border pb-4 last:border-b-0 last:pb-0"
                >
                  <img
                    src={item.thumbnail}
                    alt=""
                    className="h-16 w-16 shrink-0 rounded object-cover"
                  />
                  <div className="min-w-0 flex-1">
                    <p className="m-0 text-sm font-medium">{item.title}</p>
                    <p className="m-0 mt-1 text-sm text-text-secondary">{display}</p>
                    <div className="mt-2 flex flex-col items-stretch gap-2 sm:flex-row sm:items-center">
                      <QuantityInput
                        id={`cart-qty-${item.sku}`}
                        label={`Quantity for ${item.title}`}
                        value={item.quantity}
                        disabled={isRemoving}
                        onChange={(quantity) =>
                          dispatch(
                            updateQuantity({
                              sku: item.sku,
                              quantity,
                            })
                          )
                        }
                      />
                      <Button
                        variant="secondary"
                        className="text-sm"
                        disabled={isRemoving}
                        aria-busy={isRemoving}
                        onClick={() => {
                          void handleRemoveItem(item);
                        }}
                      >
                        {isRemoving ? 'Removing…' : 'Remove'}
                      </Button>
                    </div>
                  </div>
                </li>
              );
            })}
          </ul>

          <div className="border-t border-border pt-4">
            <p className="m-0 flex items-center justify-between text-sm font-medium">
              <span>
                Subtotal ({itemCount} item{itemCount === 1 ? '' : 's'})
              </span>
              <span>{formatCurrency(subtotal)}</span>
            </p>
          </div>
        </div>
      )}
    </Drawer>
  );
}
