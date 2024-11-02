import { useState } from 'react'
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
  const [isImageLoadingError, setImageLoadingError] = useState(false);

  const handleImageLoadingError = () => {
    setImageLoadingError(true)
  };

  const imageSrc = !isImageLoadingError
    ? ArtworkApi.getImageUrl(imageId, originalWidth)
    : ArtworkApi.getFallbackImageUrl()

  return (
    <StyledArtworkImageOuterFrame>
      <StyledArtworkImageOuterFrameBackgroundTop />
      <StyledArtworkImageOuterFrameBackgroundBottom />

      <StyledArtworkImageFrame>
        <StyledArtworkImageFrameInner>
          <ArtworkFrameGlass />

          <StyledArtworkImage
            src={imageSrc}
            alt={altText}
            $blurDataUrl={blurDataUrl}
            $originalWidth={originalWidth}
            $originalHeight={originalHeight}
            onError={handleImageLoadingError}
          />
        </StyledArtworkImageFrameInner>

        <StyledArtworkImageTitle>
          {title}
        </StyledArtworkImageTitle>
      </StyledArtworkImageFrame>
    </StyledArtworkImageOuterFrame>
  )
}