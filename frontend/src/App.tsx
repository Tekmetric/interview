import { CategoryFilter } from './components/category_filter/CategoryFilter';
import { Pagination } from './components/pagination/Pagination';
import { getTotalPages } from './components/pagination/paginationUtils';
import { ProductDetailsDrawer } from './components/product_details/ProductDetailsDrawer';
import { ProductGrid } from './components/product_grid/ProductGrid';
import { LazyPageFooter } from './components/layout/LazyPageFooter';
import { PageHeader } from './components/layout/PageHeader';
import { PaginationSkeleton } from './components/skeleton/PaginationSkeleton';
import { ProductGridSkeleton } from './components/skeleton/ProductGridSkeleton';
import { Skeleton } from './components/skeleton/Skeleton';
import { SortDropdown } from './components/sort_dropdown/SortDropdown';
import { useProductListing } from './hooks/useProductListing';

function App() {
  const {
    products,
    productTotal,
    isLoading,
    error,
    currentPage,
    categories,
    categoriesLoading,
    categoriesError,
    selectedCategorySlug,
    selectedProductId,
    trimmedQuery,
    selectedCategory,
    heading,
    handleSearch,
    handleSortChange,
    handleCategoryChange,
    handlePageChange,
    setSelectedProductId,
    sortOptionId,
  } = useProductListing();

  return (
    <div className="min-h-screen flex flex-col">
      <a
        href="#main-content"
        className="sr-only focus:not-sr-only focus:absolute focus:left-4 focus:top-4 focus:z-[60] focus:rounded focus:bg-white focus:px-4 focus:py-2 focus:shadow-lg focus:outline-2 focus:outline-offset-2 focus:outline-blue-600"
      >
        Skip to main content
      </a>
      <PageHeader onSearch={handleSearch} />
      <main id="main-content" className="flex-1 max-w-7xl mx-auto p-4 w-full">
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
            <div className="mb-4 flex flex-col items-stretch gap-4 sm:flex-row sm:items-center sm:justify-between">
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
                      aria-live="polite"
                      aria-atomic="true"
                    >
                      {productTotal} Item(s)
                    </span>
                  )
                )}
              </div>
              <SortDropdown
                value={sortOptionId}
                onChange={handleSortChange}
                className="w-full sm:w-auto"
              />
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
                  {products.length === 0 ? (
                    <p role="status" className="text-neutral-600">
                      {trimmedQuery
                        ? `No products found for "${trimmedQuery}".`
                        : selectedCategory
                          ? `No products found in ${selectedCategory.name}.`
                          : 'No products found.'}
                    </p>
                  ) : (
                    <ProductGrid
                      products={products}
                      selectedProductId={selectedProductId}
                      onOpenDetails={setSelectedProductId}
                    />
                  )}
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
