import { useGetArtworkData } from '../../services/use-get-artwork-data/use-get-artwork-data';
import { ArtworkImage } from '../../components/ArtworkImage/ArtworkImage';
import { ArtworkList } from '../../components/ArtworkList/ArtworkList';
import { Spinner } from '../../components/Spinner/Spinner';
import { ArtworkImageSpace } from '../../components/ArtworkImageSpace/ArtworkImageSpace';
import { useCallback } from 'react';
import { GuideText } from '../../components/GuideText/GuideText';

export const MainPage = () => {
  const { data: artworkList, isFetching, fetchNextPage } = useGetArtworkData()
  
  const handleLoadMoreItems = useCallback(
    async () => {
      await fetchNextPage()
    },
    []
  )
  
  return (
    <ArtworkList onScrollToEnd={handleLoadMoreItems}>
      {isFetching && (
        <ArtworkImageSpace>
          <Spinner />
        </ArtworkImageSpace>
      )}

      {artworkList?.map((artworkListItem, index) => (
        <ArtworkImageSpace key={artworkListItem.imageId}>
          {index === 0 && (
            <GuideText />
          )}

          <ArtworkImage
            imageId={artworkListItem.imageId}
            title={artworkListItem.title}
            altText={artworkListItem.altText}
            blurDataUrl={artworkListItem.blurDataURL}
            width={artworkListItem.width}
          />
        </ArtworkImageSpace>
      ))}
    </ArtworkList>
  )
}