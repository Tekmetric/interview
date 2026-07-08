import { useEffect, useState } from 'react';
import { ProductGrid } from './components/product_grid/ProductGrid';
import { PageFooter } from './components/layout/PageFooter';
import { PageHeader } from './components/layout/PageHeader';
import { SortDropdown } from './components/sort_dropdown/SortDropdown';
import { getProducts } from './hooks/getProducts';
import { getSortedProducts } from './hooks/getSortedProducts';
import { searchProducts } from './hooks/searchProducts';
import {
  DEFAULT_SORT_OPTION_ID,
  SORT_OPTIONS,
  type Product,
} from './hooks/types';

function App() {
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [sortOptionId, setSortOptionId] = useState(DEFAULT_SORT_OPTION_ID);

  useEffect(() => {
    let cancelled = false;

    async function loadProducts() {
      setIsLoading(true);

      try {
        // There is a lot more I could do for security here.
        // But for the purposes of a demo app please don't try
        // to do SQL injection or something. :)
        const trimmedQuery = searchQuery.trim();
        
        const sort = SORT_OPTIONS.find((option) => option.id === sortOptionId);
        const sortParams =
          sort?.sortBy && sort?.order
            ? { sortBy: sort.sortBy, order: sort.order }
            : {};

        const response = trimmedQuery
          ? await searchProducts({ q: trimmedQuery, limit: 12, ...sortParams })
          : sortParams.sortBy
            ? await getSortedProducts({ ...sortParams, limit: 12 })
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
  }, [searchQuery, sortOptionId]);

  const heading = searchQuery.trim()
    ? `Search results for "${searchQuery.trim()}"`
    : 'Products';

  return (
    <div className="min-h-screen flex flex-col">
      <PageHeader onSearch={setSearchQuery} />
      <main className="flex-1 max-w-7xl mx-auto p-4 w-full">
        <div className="flex items-center justify-between gap-4 mb-4">
          <h1 className="text-2xl font-bold">{heading}</h1>
          <SortDropdown value={sortOptionId} onChange={setSortOptionId} />
        </div>

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
