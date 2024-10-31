import { ArtworkApi } from '../artwork-api/ArtworkApi';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useMemo } from 'react';
import { ArtworkListItem } from '../../types/artwork-list-item';
import { ArtworkGetListResponse } from '../../types/response/ArtworkGetListResponse';

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

  const flattenData = useMemo(
    () => data?.pages.flatMap((page) => page.data) ?? [],
    [data?.pages]
  )

  const normalizedArtworkList = useMemo(
    () => flattenData.reduce((acc, {
      id,
      image_id,
      thumbnail,
      title
    }) => {
      if (image_id && thumbnail) {
        acc.push({
          id,
          imageId: image_id,
          title,
          altText: thumbnail.alt_text,
          blurDataURL: thumbnail.lqip
        })
      }

      return acc
    }, [] as ArtworkListItem[]),
    [flattenData]
  )

  return {
    data: normalizedArtworkList,
    isFetching,
    fetchNextPage
  }
}