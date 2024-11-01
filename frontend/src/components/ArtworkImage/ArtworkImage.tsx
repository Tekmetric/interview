import {
  StyledArtworkImage,
  StyledArtworkImageOuterFrame,
  StyledArtworkImageOuterFrameBackgroundBottom,
  StyledArtworkImageOuterFrameBackgroundTop,
  StyledArtworkImageFrame,
  StyledArtworkImageTitle,
  StyledArtworkImageFrameInner
} from './styled';
import { ArtworkApi } from '../../services/artwork-api/ArtworkApi';
import { ArtworkFrameGlass } from '../ArtworkFrameGlass/ArtworkFrameGlass';

type Props = {
  imageId: string;
  title: string;
  altText: string;
  blurDataUrl: string;
  width: number
}

export const ArtworkImage = ({ imageId, title, altText, blurDataUrl, width }: Props) => {
  return (
    <StyledArtworkImageOuterFrame>
      <StyledArtworkImageOuterFrameBackgroundTop />
      <StyledArtworkImageOuterFrameBackgroundBottom />

      <StyledArtworkImageFrame>
        <StyledArtworkImageFrameInner>
          <ArtworkFrameGlass />

          <StyledArtworkImage
            src={ArtworkApi.getImageUrl(imageId, width)}
            alt={altText}
            $blurDataUrl={blurDataUrl}
          />
        </StyledArtworkImageFrameInner>

        <StyledArtworkImageTitle>
          {title}
        </StyledArtworkImageTitle>
      </StyledArtworkImageFrame>
    </StyledArtworkImageOuterFrame>
  )
}