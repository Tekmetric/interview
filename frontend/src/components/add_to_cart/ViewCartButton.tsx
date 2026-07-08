import { useState } from 'react';
import { Button } from '../button/Button';
import { CartDrawer } from './CartDrawer';

export function ViewCartButton() {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <>
      <Button
        variant="secondary"
        className="flex items-center gap-2"
        aria-label="View cart"
        aria-expanded={isOpen}
        onClick={() => setIsOpen(true)}
      >
        <span aria-hidden="true">🛒</span>
        <span className="md:hidden">Cart</span>
        <span className="hidden md:inline">View Cart</span>
      </Button>
      <CartDrawer isOpen={isOpen} onClose={() => setIsOpen(false)} />
    </>
  );
}
