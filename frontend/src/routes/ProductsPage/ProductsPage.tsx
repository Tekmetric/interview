import { useMemo } from 'react';
import ProductsGrid from '../../components/ProductsGrid/ProductsGrid.tsx';
import { useFilter } from '../../providers/FilterProvider.tsx';
import Filter from '../../components/Filter/Filter.tsx';
import usePaginatedProducts from '../../hooks/usePaginatedProducts.tsx';
import ProductsError from '../../components/ProductsGrid/ProductsError.tsx';

const ProductsPage = () => {
  const { searchTerm } = useFilter();
  const { data, fetchNextPage, isFetching, hasNextPage, isLoading, error } =
    usePaginatedProducts(searchTerm);

  // Flatten all products from all pages
  const allProducts = useMemo(
    () => data?.pages.flatMap((page) => page.products) || [],
    [data]
  );

  if (error) return <ProductsError />;

  return (
    <div className="flex h-full flex-col gap-5">
      <Filter />
      <ProductsGrid
        products={allProducts}
        showLoadMore={hasNextPage}
        isFetching={isFetching}
        isLoading={isLoading}
        fetchNextPage={fetchNextPage}
      />
    </div>
  );
};

export default ProductsPage;
