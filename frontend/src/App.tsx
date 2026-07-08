import { useEffect, useState } from 'react';
import { ProductGrid } from './components/product_grid/ProductGrid';
import { PageFooter } from './components/layout/PageFooter';
import { PageHeader } from './components/layout/PageHeader';
import { getProducts } from './hooks/getProducts';
import { searchProducts } from './hooks/searchProducts';
import type { Product } from './hooks/types';

function App() {
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    let cancelled = false;

    async function loadProducts() {
      setIsLoading(true);

      try {
        // There is a lot more I could do for security here.
        // But for the purposes of a demo app please don't try
        // to do SQL injection or something. :)
        const trimmedQuery = searchQuery.trim();
        const response = trimmedQuery
          ? await searchProducts({ q: trimmedQuery, limit: 12 })
          : await getProducts({ limit: 12 });

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
  }, [searchQuery]);

  const heading = searchQuery.trim()
    ? `Search results for "${searchQuery.trim()}"`
    : 'Products';

  return (
    <div className="min-h-screen flex flex-col">
      <PageHeader onSearch={setSearchQuery} />
      <main className="flex-1 max-w-7xl mx-auto p-4 w-full">
        <h1 className="text-2xl font-bold mb-4">{heading}</h1>

        {isLoading && (
          <p role="status" aria-live="polite">
            Loading products...
          </p>
        )}

        {error && <p role="alert">{error}</p>}

        {!isLoading && !error && <ProductGrid products={products} />}
      </main>
      <PageFooter />
    </div>
  );
}

export default App;
