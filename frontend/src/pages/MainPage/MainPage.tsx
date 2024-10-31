import { useGetArtworkData } from '../../services/use-get-artwork-data/use-get-artwork-data';
import { ArtworkImage } from '../../components/ArtworkImage/ArtworkImage';
import { ArtworkList } from '../../components/ArtworkList/ArtworkList';


export const MainPage = () => {
  const { artworkList, isLoading } = useGetArtworkData()
  
  return (
    <ArtworkList>
      {artworkList?.map((artworkListItem) => (
        <ArtworkImage
          key={artworkListItem.imageId}
          imageId={artworkListItem.imageId}
          altText={artworkListItem.altText}
          blurDataUrl={artworkListItem.blurDataURL}
        />
      ))}
    </ArtworkList>
  )
}