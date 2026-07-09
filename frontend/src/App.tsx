import { useEffect, useState } from 'react';
import { CategoryFilter } from './components/category_filter/CategoryFilter';
import { Pagination } from './components/pagination/Pagination';
import {
  getTotalPages,
  pageToSkip,
  PRODUCTS_PAGE_SIZE,
} from './components/pagination/paginationUtils';
import { ProductDetailsDrawer } from './components/product_details/ProductDetailsDrawer';
import { ProductGrid } from './components/product_grid/ProductGrid';
import { LazyPageFooter } from './components/layout/LazyPageFooter';
import { PageHeader } from './components/layout/PageHeader';
import { PaginationSkeleton } from './components/skeleton/PaginationSkeleton';
import { ProductGridSkeleton } from './components/skeleton/ProductGridSkeleton';
import { Skeleton } from './components/skeleton/Skeleton';
import { SortDropdown } from './components/sort_dropdown/SortDropdown';
import { getCategories } from './hooks/getCategories';
import { getProducts } from './hooks/getProducts';
import { getProductsByCategory } from './hooks/getProductsByCategory';
import { getSortedProducts } from './hooks/getSortedProducts';
import { searchProducts } from './hooks/searchProducts';
import {
  DEFAULT_SORT_OPTION_ID,
  SORT_OPTIONS,
} from './hooks/sortOptions';
import {
  type ProductSummary,
  type ProductCategory,
  type GetProductsParams,
  type ProductsResponse,
  type ProductSortField,
  type SortOrder,
} from './hooks/types';
import { scrollToTopRespectingMotion } from './utils/scrollIntoViewRespectingMotion';

interface BuildProductFetchParams {
  trimmedQuery: string;
  selectedCategorySlug: string | null;
  sortParams: { sortBy?: ProductSortField; order?: SortOrder };
  paginationParams: GetProductsParams;
}

// Resolves which product fetcher to call based on active filters.
function buildProductFetch({
  trimmedQuery,
  selectedCategorySlug,
  sortParams,
  paginationParams,
}: BuildProductFetchParams): Promise<ProductsResponse> {
  if (trimmedQuery) {
    return searchProducts({ q: trimmedQuery, ...paginationParams, ...sortParams });
  }

  if (selectedCategorySlug) {
    return getProductsByCategory({
      category: selectedCategorySlug,
      ...paginationParams,
      ...sortParams,
    });
  }

  if (sortParams.sortBy && sortParams.order) {
    return getSortedProducts({
      sortBy: sortParams.sortBy,
      order: sortParams.order,
      ...paginationParams,
    });
  }

  return getProducts(paginationParams);
}

function App() {
  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [productTotal, setProductTotal] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [sortOptionId, setSortOptionId] = useState(DEFAULT_SORT_OPTION_ID);
  const [selectedCategorySlug, setSelectedCategorySlug] = useState<string | null>(
    null
  );
  const [selectedProductId, setSelectedProductId] = useState<number | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
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

        const paginationParams = {
          limit: PRODUCTS_PAGE_SIZE,
          skip: pageToSkip(currentPage),
        };

        const response = await buildProductFetch({
          trimmedQuery,
          selectedCategorySlug,
          sortParams,
          paginationParams,
        });

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
  }, [searchQuery, sortOptionId, selectedCategorySlug, currentPage]);

  const handleSearch = (query: string) => {
    setSearchQuery(query);
    setCurrentPage(1);
  };

  const handleSortChange = (id: string) => {
    setSortOptionId(id);
    setCurrentPage(1);
  };

  const handleCategoryChange = (slug: string | null) => {
    setSelectedCategorySlug(slug);
    setCurrentPage(1);
    scrollToTopRespectingMotion();
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    scrollToTopRespectingMotion();
  };

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
      <PageHeader onSearch={handleSearch} />
      <main className="flex-1 max-w-7xl mx-auto p-4 w-full">
        <div className="flex flex-col gap-6 lg:flex-row lg:items-start">
          <CategoryFilter
            categories={categories}
            isLoading={categoriesLoading}
            error={categoriesError}
            isSearchActive={Boolean(trimmedQuery)}
            value={selectedCategorySlug}
            onChange={handleCategoryChange}
          />

          <div className="min-w-0 flex-1">
            <div className="flex items-center justify-between gap-4 mb-4">
              <div className="flex flex-wrap items-baseline gap-x-2 gap-y-1">
                <h1 className="text-2xl font-bold">
                  {heading}
                </h1>
                {isLoading ? (
                  <Skeleton className="h-4 w-20" />
                ) : (
                  !error && (
                    <span
                      className="text-sm text-neutral-600"
                      aria-label={`${productTotal} items`}
                    >
                      {productTotal} Item(s)
                    </span>
                  )
                )}
              </div>
              <SortDropdown value={sortOptionId} onChange={handleSortChange} />
            </div>

            {error && <p role="alert">{error}</p>}

            {isLoading ? (
              <>
                <ProductGridSkeleton />
                <PaginationSkeleton />
              </>
            ) : (
              !error && (
                <>
                  <ProductGrid
                    products={products}
                    selectedProductId={selectedProductId}
                    onOpenDetails={setSelectedProductId}
                  />
                  <Pagination
                    currentPage={currentPage}
                    totalPages={getTotalPages(productTotal)}
                    onPageChange={handlePageChange}
                  />
                </>
              )
            )}
          </div>
        </div>
      </main>
      <LazyPageFooter />
      <ProductDetailsDrawer
        productId={selectedProductId}
        isOpen={selectedProductId !== null}
        onClose={() => setSelectedProductId(null)}
      />
    </div>
  );
}

export default App;
