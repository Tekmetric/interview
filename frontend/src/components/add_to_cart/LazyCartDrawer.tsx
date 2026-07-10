import { lazy, Suspense } from 'react';

const CartDrawer = lazy(() =>
  import('./CartDrawer').then((module) => ({ default: module.CartDrawer }))
);

interface LazyCartDrawerProps {
  isOpen: boolean;
  onClose: () => void;
}

export function LazyCartDrawer({ isOpen, onClose }: LazyCartDrawerProps) {
  if (!isOpen) {
    return null;
  }

  return (
    <Suspense fallback={null}>
      <CartDrawer isOpen={isOpen} onClose={onClose} />
    </Suspense>
  );
}
