import {
  StyledArtworkImage,
  StyledArtworkImageOuterFrame,
  StyledArtworkImageOuterFrameBackgroundBottom,
  StyledArtworkImageOuterFrameBackgroundTop,
  StyledArtworkImageFrame,
  StyledArtworkImageTitle,
  StyledArtworkImageFrameShadow
} from './styled';
import { ArtworkApi } from '../../services/artwork-api/ArtworkApi';

type Props = {
  imageId: string;
  title: string;
  altText: string;
  blurDataUrl: string;
}

export const ArtworkImage = ({ imageId, title, altText, blurDataUrl }: Props) => {
  return (
    <StyledArtworkImageOuterFrame>
      <StyledArtworkImageOuterFrameBackgroundTop />
      <StyledArtworkImageOuterFrameBackgroundBottom />

      <StyledArtworkImageFrame>
        <StyledArtworkImageFrameShadow>
          <StyledArtworkImage
            src={ArtworkApi.getImageUrl(imageId)}
            alt={altText}
            blurDataUrl={blurDataUrl}
          />
        </StyledArtworkImageFrameShadow>

        <StyledArtworkImageTitle>
          {title}
        </StyledArtworkImageTitle>
      </StyledArtworkImageFrame>
    </StyledArtworkImageOuterFrame>
  )
}