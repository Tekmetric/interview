import {
  StyledArtworkImage,
  StyledArtworkImageFrame,
  StyledArtworkImageFrameBackgroundBottom,
  StyledArtworkImageFrameBackgroundTop,
  StyledArtworkImageSpace,
  StyledLightOverlay
} from './styled';
import { ArtworkApi } from '../../services/artwork-api/ArtworkApi';

type Props = {
  imageId: string
  altText: string
  blurDataUrl: string
}

export const ArtworkImage = ({ imageId, altText, blurDataUrl }: Props) => {
  return (
    <StyledArtworkImageSpace>
      <StyledArtworkImageFrame>
        <StyledArtworkImageFrameBackgroundTop />
        <StyledArtworkImageFrameBackgroundBottom />

        <StyledArtworkImage
          src={ArtworkApi.getImageUrl(imageId)}
          alt={altText}
          blurDataUrl={blurDataUrl}
        />
      </StyledArtworkImageFrame>

      <StyledLightOverlay />
    </StyledArtworkImageSpace>
  )
}