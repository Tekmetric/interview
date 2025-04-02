import { useInfiniteQuery } from '@tanstack/react-query';
import { getProducts } from '../api/product.ts';

const usePaginatedProducts = (searchTerm: string) => {
  const { data, fetchNextPage, isLoading, hasNextPage, isFetching, error } =
    useInfiniteQuery({
      queryKey: ['products', searchTerm],
      queryFn: ({ pageParam }) =>
        getProducts({ pageParam, search: searchTerm }),
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

  return { data, fetchNextPage, isLoading, hasNextPage, isFetching, error };
};

export default usePaginatedProducts;
