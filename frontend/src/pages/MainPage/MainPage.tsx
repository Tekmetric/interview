import { useGetArtworkData } from '../../services/use-get-artwork-data/use-get-artwork-data';
import { ArtworkImage } from '../../components/ArtworkImage/ArtworkImage';
import { ArtworkList } from '../../components/ArtworkList/ArtworkList';
import { Spinner } from '../../components/Spinner/Spinner';
import { ArtworkImageSpace } from '../../components/ArtworkImageSpace/ArtworkImageSpace';

export const MainPage = () => {
  const { artworkList, isLoading } = useGetArtworkData()
  
  return (
    <ArtworkList>
      {isLoading && (
        <ArtworkImageSpace>
          <Spinner />
        </ArtworkImageSpace>
      )}

      {artworkList?.map((artworkListItem) => (
        <ArtworkImageSpace key={artworkListItem.imageId}>
          <ArtworkImage
            imageId={artworkListItem.imageId}
            title={artworkListItem.title}
            altText={artworkListItem.altText}
            blurDataUrl={artworkListItem.blurDataURL}
          />
        </ArtworkImageSpace>
      ))}
    </ArtworkList>
  )
}