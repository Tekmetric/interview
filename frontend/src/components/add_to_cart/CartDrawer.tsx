import { Button } from '../button/Button';
import { Drawer } from '../drawer/Drawer';
import { QuantityInput } from '../quantity_input/QuantityInput';
import { formatProductPrice } from '../product_card/priceUtils';
import { removeItem, updateQuantity } from '../../store/cartSlice';
import {
  selectCartItemCount,
  selectCartItems,
  selectCartSubtotal,
} from '../../store/cartSelectors';
import { useAppDispatch, useAppSelector } from '../../store/hooks';

interface CartDrawerProps {
  isOpen: boolean;
  onClose: () => void;
}

const usdFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

export function CartDrawer({ isOpen, onClose }: CartDrawerProps) {
  const dispatch = useAppDispatch();
  const items = useAppSelector(selectCartItems);
  const itemCount = useAppSelector(selectCartItemCount);
  const subtotal = useAppSelector(selectCartSubtotal);
  const isEmpty = items.length === 0;

  return (
    <Drawer
      isOpen={isOpen}
      onClose={onClose}
      title="Cart"
      titleId="cart-drawer-title"
      closeAriaLabel="Close cart"
      panelClassName="drawer-panel--full-height"
    >
      {isEmpty ? (
        <p className="m-0 text-sm text-neutral-600">Cart is empty</p>
      ) : (
        <div className="flex flex-col gap-4">
          <ul className="m-0 flex list-none flex-col gap-4 p-0">
            {items.map((item) => {
              const { display } = formatProductPrice(
                item.price,
                item.discountPercentage
              );

              return (
                <li
                  key={item.sku}
                  className="flex gap-3 border-b border-neutral-200 pb-4 last:border-b-0 last:pb-0"
                >
                  <img
                    src={item.thumbnail}
                    alt=""
                    className="h-16 w-16 shrink-0 rounded object-cover"
                  />
                  <div className="min-w-0 flex-1">
                    <p className="m-0 text-sm font-medium">{item.title}</p>
                    <p className="m-0 mt-1 text-sm text-neutral-600">{display}</p>
                    <div className="mt-2 flex items-center gap-2">
                      <QuantityInput
                        id={`cart-qty-${item.sku}`}
                        label={`Quantity for ${item.title}`}
                        value={item.quantity}
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
                        onClick={() => dispatch(removeItem(item.sku))}
                      >
                        Remove
                      </Button>
                    </div>
                  </div>
                </li>
              );
            })}
          </ul>

          <div className="border-t border-neutral-200 pt-4">
            <p className="m-0 flex items-center justify-between text-sm font-medium">
              <span>
                Subtotal ({itemCount} item{itemCount === 1 ? '' : 's'})
              </span>
              <span>{usdFormatter.format(subtotal)}</span>
            </p>
          </div>
        </div>
      )}
    </Drawer>
  );
}
