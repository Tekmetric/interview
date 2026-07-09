import { useEffect, useState } from 'react';
import { getProduct } from '../../hooks/getProduct';
import type { ProductDetail } from '../../hooks/types';
import { Drawer } from '../drawer/Drawer';
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
  const [product, setProduct] = useState<ProductDetail | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!isOpen || productId === null) {
      return;
    }

    const id = productId;
    let cancelled = false;

    async function loadProduct() {
      setIsLoading(true);
      setProduct(null);
      setError(null);

      try {
        const result = await getProduct(id);

        if (!cancelled) {
          setProduct(result);
        }
      } catch {
        if (!cancelled) {
          setError('Failed to load product details.');
        }
      } finally {
        if (!cancelled) {
          setIsLoading(false);
        }
      }
    }

    loadProduct();

    return () => {
      cancelled = true;
    };
  }, [isOpen, productId]);

  const title = product?.title ?? 'Product Details';

  return (
    <Drawer
      isOpen={isOpen}
      onClose={onClose}
      title={title}
      titleId="product-details-drawer-title"
      closeAriaLabel="Close product details"
      panelClassName="drawer-panel--wide"
    >
      {isLoading && (
        <p role="status" aria-live="polite" className="text-sm text-neutral-600">
          Loading product details...
        </p>
      )}

      {error && (
        <p role="alert" className="text-sm text-red-600">
          {error}
        </p>
      )}

      {product && (
        <ProductDetailsContent product={product} onNotifySuccess={onClose} />
      )}
    </Drawer>
  );
}
