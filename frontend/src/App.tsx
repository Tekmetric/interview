import { useEffect, useState } from 'react';
import { CategoryFilter } from './components/category_filter/CategoryFilter';
import { ProductGrid } from './components/product_grid/ProductGrid';
import { PageFooter } from './components/layout/PageFooter';
import { PageHeader } from './components/layout/PageHeader';
import { SortDropdown } from './components/sort_dropdown/SortDropdown';
import { getCategories } from './hooks/getCategories';
import { getProducts } from './hooks/getProducts';
import { getProductsByCategory } from './hooks/getProductsByCategory';
import { getSortedProducts } from './hooks/getSortedProducts';
import { searchProducts } from './hooks/searchProducts';
import {
  DEFAULT_SORT_OPTION_ID,
  SORT_OPTIONS,
  type Product,
  type ProductCategory,
} from './hooks/types';

function App() {
  const [products, setProducts] = useState<Product[]>([]);
  const [productTotal, setProductTotal] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [sortOptionId, setSortOptionId] = useState(DEFAULT_SORT_OPTION_ID);
  const [selectedCategorySlug, setSelectedCategorySlug] = useState<string | null>(
    null
  );
  const [categories, setCategories] = useState<ProductCategory[]>([]);
  const [categoriesLoading, setCategoriesLoading] = useState(true);
  const [categoriesError, setCategoriesError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function loadCategories() {
      setCategoriesLoading(true);

      try {
        const response = await getCategories();

        if (!cancelled) {
          setCategories(response);
          setCategoriesError(null);
        }
      } catch {
        if (!cancelled) {
          setCategoriesError('Failed to load categories.');
        }
      } finally {
        if (!cancelled) {
          setCategoriesLoading(false);
        }
      }
    }

    loadCategories();

    return () => {
      cancelled = true;
    };
  }, []);

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
          : selectedCategorySlug
            ? await getProductsByCategory({
                category: selectedCategorySlug,
                limit: 12,
                ...sortParams,
              })
            : sortParams.sortBy
              ? await getSortedProducts({ ...sortParams, limit: 12 })
              : await getProducts({ limit: 12 });

        if (!cancelled) {
          setProducts(response.products);
          setProductTotal(response.total);
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
  }, [searchQuery, sortOptionId, selectedCategorySlug]);

  const trimmedQuery = searchQuery.trim();
  const selectedCategory = categories.find(
    (category) => category.slug === selectedCategorySlug
  );

  const heading = trimmedQuery
    ? `Search results for "${trimmedQuery}"`
    : selectedCategory
      ? selectedCategory.name
      : 'Products';

  return (
    <div className="min-h-screen flex flex-col">
      <PageHeader onSearch={setSearchQuery} />
      <main className="flex-1 max-w-7xl mx-auto p-4 w-full">
        <div className="flex flex-col gap-6 lg:flex-row lg:items-start">
          <CategoryFilter
            categories={categories}
            isLoading={categoriesLoading}
            error={categoriesError}
            isSearchActive={Boolean(trimmedQuery)}
            value={selectedCategorySlug}
            onChange={setSelectedCategorySlug}
          />

          <div className="min-w-0 flex-1">
            <div className="flex items-center justify-between gap-4 mb-4">
              <div className="flex flex-wrap items-baseline gap-x-2 gap-y-1">
                <h1 className="text-2xl font-bold">{heading}</h1>
                {!isLoading && !error && (
                  <span
                    className="text-sm text-neutral-600"
                    aria-label={`${productTotal} items`}
                  >
                    {productTotal} Item(s)
                  </span>
                )}
              </div>
              <SortDropdown value={sortOptionId} onChange={setSortOptionId} />
            </div>

            {isLoading && (
              <p role="status" aria-live="polite">
                Loading products...
              </p>
            )}

            {error && <p role="alert">{error}</p>}

            {!isLoading && !error && <ProductGrid products={products} />}
          </div>
        </div>
      </main>
      <PageFooter />
    </div>
  );
}

export default App;
