import { PropsWithChildren } from 'react'

import { StyledArtworkImageWall } from './styles'

export const ArtworkImageWall = ({ children }: PropsWithChildren) => (
  <StyledArtworkImageWall>{children}</StyledArtworkImageWall>
)
