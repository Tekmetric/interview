import { useCallback } from 'react'

import { ArtworkGallery } from '../../components/ArtworkGallery/ArtworkGallery'
import { ArtworkImage } from '../../components/ArtworkImage/ArtworkImage'
import { ArtworkImageWall } from '../../components/ArtworkImageWall/ArtworkImageWall'
import { SINGLE_LIGHT_SOURCE_ARTWORK_ASPECT_RATIO_THRESHOLD } from '../../components/ArtworkImageWall/constants'
import { GuideText } from '../../components/GuideText/GuideText'
import { Logo } from '../../components/Logo/Logo'
import { Spinner } from '../../components/Spinner/Spinner'
import { useGetArtworkData } from './services/use-get-artwork-data/use-get-artwork-data'

export const MainPage = () => {
  const { data: artworkList, isFetching, fetchNextPage } = useGetArtworkData()

  const handleLoadMoreItems = useCallback(async () => {
    await fetchNextPage()
  }, [fetchNextPage])

  return (
    <>
      <Logo />

      <ArtworkGallery onScrollToEnd={handleLoadMoreItems}>
        {isFetching && !artworkList.length && (
          <ArtworkImageWall lightSources='none'>
            <Spinner />
          </ArtworkImageWall>
        )}

        {artworkList?.map((artworkListItem, index) => (
          <ArtworkImageWall
            key={artworkListItem.imageId}
            lightSources={
              artworkListItem.aspectRatio <
              SINGLE_LIGHT_SOURCE_ARTWORK_ASPECT_RATIO_THRESHOLD
                ? 'single'
                : 'double'
            }
          >
            {index === 0 && <GuideText />}

            <ArtworkImage
              imageId={artworkListItem.imageId}
              title={artworkListItem.title}
              description={artworkListItem.description}
              date={artworkListItem.date}
              artist={artworkListItem.artist}
              altText={artworkListItem.altText}
              blurDataUrl={artworkListItem.blurDataURL}
              originalWidth={artworkListItem.originalWidth}
              originalHeight={artworkListItem.originalHeight}
            />
          </ArtworkImageWall>
        ))}
      </ArtworkGallery>
    </>
  )
}
