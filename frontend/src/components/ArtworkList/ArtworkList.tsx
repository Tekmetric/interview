import { PropsWithChildren } from 'react';
import { StyledArtworkList } from './styled';

export const ArtworkList = ({ children }: PropsWithChildren) => (
  <StyledArtworkList>
    {children}
  </StyledArtworkList>
)