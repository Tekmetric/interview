import { useEffect, useState } from 'react';
import { ProductGrid } from './components/ProductGrid';
import { getProducts } from './hooks/getProducts';
import type { Product } from './hooks/types';

function App() {
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function loadProducts() {
      try {
        const response = await getProducts({ limit: 12 });
        if (!cancelled) {
          setProducts(response.products);
          setError(null);
        }
      } catch {
        if (!cancelled) {
          setError('Failed to load products. Please try again later.');
        }
      } finally {
        if (!cancelled) {
          setIsLoading(false);
        }
      }
    }

    loadProducts();

    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <main className="max-w-7xl mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">Products</h1>

      {isLoading && (
        <p role="status" aria-live="polite">
          Loading products...
        </p>
      )}

      {error && <p role="alert">{error}</p>}

      {!isLoading && !error && <ProductGrid products={products} />}
    </main>
  );
}

export default App;
