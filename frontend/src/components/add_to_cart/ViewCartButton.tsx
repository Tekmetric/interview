import { useState } from 'react';
import { Button } from '../button/Button';
import { CART_DRAWER_PANEL_ID } from './cartDrawerConstants';
import { LazyCartDrawer } from './LazyCartDrawer';
import { selectCartItemCount } from '../../store/cartSelectors';
import { useAppSelector } from '../../store/hooks';

export function ViewCartButton() {
  const [isOpen, setIsOpen] = useState(false);
  const itemCount = useAppSelector(selectCartItemCount);

  return (
    <>
      <Button
        variant="secondary"
        className="relative flex items-center gap-2"
        aria-haspopup="dialog"
        aria-controls={CART_DRAWER_PANEL_ID}
        aria-label={
          itemCount > 0 ? `View cart, ${itemCount} items` : 'View cart'
        }
        aria-expanded={isOpen}
        onClick={() => setIsOpen(true)}
      >
        <span aria-hidden="true">🛒</span>
        <span className="md:hidden">Cart</span>
        <span className="hidden md:inline">View Cart</span>
        {itemCount > 0 && (
          <span
            className="inline-flex min-w-5 items-center justify-center rounded-full bg-badge px-1.5 py-0.5 text-xs font-semibold text-on-badge"
            aria-hidden="true"
          >
            {itemCount}
          </span>
        )}
      </Button>
      <LazyCartDrawer isOpen={isOpen} onClose={() => setIsOpen(false)} />
    </>
  );
}
