import { useCallback, useMemo } from 'react';
import useInfinityQueryArtCrimes from '@/hooks/useInfinityQueryArtCrimes';
import { ArtCrimeQueryParams } from '@/types/artCrime';

type ArtCrimesListProps = {
  queryParams: ArtCrimeQueryParams;
};

export function useArtCrimesList({ queryParams }: ArtCrimesListProps) {
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoading,
    error,
    refetch,
    isRefetching,
  } = useInfinityQueryArtCrimes(queryParams);

  const items = useMemo(() => {
    return data?.pages.flatMap((page) => page.items) ?? [];
  }, [data]);

  const loadMore = useCallback(() => {
    if (hasNextPage && !isFetchingNextPage) fetchNextPage();
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  return {
    items,
    isLoading,
    error,
    isFetchingNextPage,
    loadMore,
    refetch,
    isRefetching,
  };
}
