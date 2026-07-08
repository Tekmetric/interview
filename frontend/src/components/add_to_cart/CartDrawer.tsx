import { Drawer } from '../drawer/Drawer';

interface CartDrawerProps {
  isOpen: boolean;
  onClose: () => void;
}

export function CartDrawer({ isOpen, onClose }: CartDrawerProps) {
  return (
    <Drawer
      isOpen={isOpen}
      onClose={onClose}
      title="Cart"
      titleId="cart-drawer-title"
      closeAriaLabel="Close cart"
    >
      <p className="m-0 text-sm text-neutral-600">Cart is empty</p>
    </Drawer>
  );
}
