import { ArtworkApi } from '../artwork-api/ArtworkApi';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useMemo } from 'react';
import { ArtworkGetListResponse } from '../../types/response/ArtworkGetListResponse';
import { normalizeArtworkData } from './services/normalize-artwork-data/normalize-artwork-data';

export const useGetArtworkData = () => {
  const {
    data,
    isFetching,
    fetchNextPage
  } = useInfiniteQuery({
    initialPageParam: 1,
    getNextPageParam: (lastPage: ArtworkGetListResponse) => {
      return lastPage.pagination.current_page < lastPage.pagination.total_pages
        ? lastPage.pagination.current_page + 1
        : null
    },
    queryKey: ['artwork-list'],
    queryFn: ({ pageParam }) => ArtworkApi.getList({ page: pageParam })
  })

  const normalizedArtworkList = useMemo(
    () => normalizeArtworkData(data?.pages ?? []),
    [data?.pages]
  )

  return {
    data: normalizedArtworkList,
    isFetching,
    fetchNextPage
  }
}