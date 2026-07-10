import { Drawer } from '../drawer/Drawer';
import { useProduct } from '../../hooks/useProduct';
import { ProductDetailsContent } from './ProductDetailsContent';

interface ProductDetailsDrawerProps {
  productId: number | null;
  isOpen: boolean;
  onClose: () => void;
}

export function ProductDetailsDrawer({
  productId,
  isOpen,
  onClose,
}: ProductDetailsDrawerProps) {
  const { product, isLoading, error } = useProduct(productId, isOpen);

  const title = product?.title ?? 'Product Details';

  return (
    <Drawer
      isOpen={isOpen}
      onClose={onClose}
      title={title}
      titleId="product-details-drawer-title"
      panelId="product-details-drawer-panel"
      closeAriaLabel="Close product details"
      panelClassName="drawer-panel--wide drawer-panel--full-height"
    >
      {isLoading && (
        <p role="status" aria-live="polite" className="text-sm text-text-secondary">
          Loading product details...
        </p>
      )}

      {error && (
        <p role="alert" className="text-sm text-error">
          {error}
        </p>
      )}

      {product && <ProductDetailsContent product={product} onClose={onClose} />}
    </Drawer>
  );
}
