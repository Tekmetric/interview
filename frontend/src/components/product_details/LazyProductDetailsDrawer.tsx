import { lazy, Suspense } from 'react';

const ProductDetailsDrawer = lazy(() =>
  import('./ProductDetailsDrawer').then((module) => ({
    default: module.ProductDetailsDrawer,
  }))
);

interface LazyProductDetailsDrawerProps {
  productId: number | null;
  isOpen: boolean;
  onClose: () => void;
}

export function LazyProductDetailsDrawer(props: LazyProductDetailsDrawerProps) {
  if (!props.isOpen) {
    return null;
  }

  return (
    <Suspense fallback={null}>
      <ProductDetailsDrawer {...props} />
    </Suspense>
  );
}
