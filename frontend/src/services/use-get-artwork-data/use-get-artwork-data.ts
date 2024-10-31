import { ArtworkApi } from '../artwork-api/ArtworkApi';
import { useQuery } from '@tanstack/react-query';
import { useMemo } from 'react';
import { ArtworkListItem } from '../../types/artwork-list-item';

export const useGetArtworkData = () => {
  const { data: artworkList, isLoading: artworkListLoading } = useQuery({
    queryKey: ['artwork-list'],
    queryFn: () => ArtworkApi.getList({ page: 1 })
  })

  const normalizedArtworkList = useMemo(
    () => artworkList?.data.reduce((acc, {
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
    [artworkList?.data]
  )

  return {
    artworkList: normalizedArtworkList,
    isLoading: artworkListLoading
  }
}