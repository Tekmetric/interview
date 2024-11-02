import { useState } from 'react'
import {
  StyledArtworkImage,
  StyledArtworkImageOuterFrame,
  StyledArtworkImageOuterFrameBackgroundBottom,
  StyledArtworkImageOuterFrameBackgroundTop,
  StyledArtworkImageFrame,
  StyledArtworkImageFrameInner
} from './styled';
import { ArtworkApi } from '../../services/artwork-api/ArtworkApi';
import { ArtworkFrameGlass } from '../ArtworkFrameGlass/ArtworkFrameGlass';
import { ArtworkImageTitle } from '../ArtworkImageTitle/ArtworkImageTitle';
import { ArtworkAdditionalInfo } from '../ArtworkAdditionalInfo/ArtworkAdditionalInfo';

type Props = {
  imageId: string;
  title: string;
  description: string | null;
  date: string | null;
  artist: string | null;
  altText: string;
  blurDataUrl: string;
  originalWidth: number
  originalHeight: number
}

export const ArtworkImage = ({
  imageId,
  title,
  description,
  date,
  artist,
  altText,
  blurDataUrl,
  originalWidth,
  originalHeight
}: Props) => {
  const [isImageLoadingError, setImageLoadingError] = useState(false);
  const [showArtworkInfo, setShowArtworkInfo] = useState(false)

  const handleLoadingError = () => {
    setImageLoadingError(true)
  };

  const toggleArtworkInfoVisibility = () => {
    setShowArtworkInfo(!showArtworkInfo)
  }

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

          <ArtworkAdditionalInfo
            visible={showArtworkInfo}
            title={title}
            description={description}
            artist={artist}
            date={date}
            onClose={toggleArtworkInfoVisibility}
          />

          <StyledArtworkImage
            src={imageSrc}
            alt={altText}
            $blurDataUrl={blurDataUrl}
            $originalWidth={originalWidth}
            $originalHeight={originalHeight}
            onError={handleLoadingError}
            onClick={toggleArtworkInfoVisibility}
          />
        </StyledArtworkImageFrameInner>

        <ArtworkImageTitle
          title={title}
          artist={artist}
          date={date}
        />
      </StyledArtworkImageFrame>
    </StyledArtworkImageOuterFrame>
  )
}