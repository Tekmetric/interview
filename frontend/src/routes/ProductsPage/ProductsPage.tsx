import { useMemo } from 'react';

import ProductsGrid from '../../components/ProductsGrid/ProductsGrid.tsx';
import { getProducts } from '../../api/product.ts';

import { useInfiniteQuery } from '@tanstack/react-query';

const ProductsPage = () => {
  const { data, fetchNextPage, isLoading, hasNextPage, isFetching, error } =
    useInfiniteQuery({
      queryKey: ['products'],
      queryFn: getProducts,
      initialPageParam: 0,
      refetchOnWindowFocus: false,
      staleTime: 1000 * 60 * 5, // 5 minutes
      getNextPageParam: (lastPage, _allPages, lastPageParam) => {
        if (lastPage.products.length + lastPage.skip >= lastPage.total) {
          return undefined;
        }

        return lastPageParam + lastPage.limit;
      },
    });

  // Flatten all products from all pages
  const allProducts = useMemo(
    () => data?.pages.flatMap((page) => page.products) || [],
    [data]
  );

  if (error) return <div>Error loading products</div>;
  if (isLoading) return <div>Loading products...</div>;

  return (
    <div className="flex h-full flex-col gap-5">
      <ProductsGrid
        products={allProducts}
        showLoadMore={hasNextPage}
        isFetching={isFetching}
        fetchNextPage={fetchNextPage}
      />
    </div>
  );
};

export default ProductsPage;
