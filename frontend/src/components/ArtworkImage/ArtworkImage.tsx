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
  originalWidth: number
  originalHeight: number
}

export const ArtworkImage = ({ imageId, title, altText, blurDataUrl, originalWidth, originalHeight }: Props) => {
  const isLandscapeOrientation = originalWidth >= originalHeight;

  return (
    <StyledArtworkImageOuterFrame>
      <StyledArtworkImageOuterFrameBackgroundTop />
      <StyledArtworkImageOuterFrameBackgroundBottom />

      <StyledArtworkImageFrame>
        <StyledArtworkImageFrameInner>
          <ArtworkFrameGlass />

          <StyledArtworkImage
            src={ArtworkApi.getImageUrl(imageId, originalWidth)}
            alt={altText}
            $blurDataUrl={blurDataUrl}
            $originalWidth={originalWidth}
            $originalHeight={originalHeight}
            $isLandscapeOrientation={isLandscapeOrientation}
          />
        </StyledArtworkImageFrameInner>

        <StyledArtworkImageTitle>
          {title}
        </StyledArtworkImageTitle>
      </StyledArtworkImageFrame>
    </StyledArtworkImageOuterFrame>
  )
}