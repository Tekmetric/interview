import { PropsWithChildren } from 'react'

import { StyledArtworkImageWall } from './styles'
import { ArtworkImageWallLightSources } from './types'

type Props = {
  lightSources: ArtworkImageWallLightSources
}

export const ArtworkImageWall = ({
  children,
  lightSources
}: PropsWithChildren<Props>) => (
  <StyledArtworkImageWall $lightSources={lightSources}>
    {children}
  </StyledArtworkImageWall>
)
