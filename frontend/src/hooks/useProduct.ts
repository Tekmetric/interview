import { useEffect, useState } from 'react';
import { getProduct } from '../api/products/getProduct';
import type { ProductDetail } from '../types/product';

export function useProduct(productId: number | null, enabled: boolean) {
  const [product, setProduct] = useState<ProductDetail | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!enabled || productId === null) {
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

    void loadProduct();

    return () => {
      cancelled = true;
    };
  }, [enabled, productId]);

  return { product, isLoading, error };
}
