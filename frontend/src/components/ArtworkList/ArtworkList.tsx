import { PropsWithChildren } from 'react';
import { StyledArtworkList } from './styled';
import { useOnScrollToEnd } from '../../services/use-on-scroll-to-end/use-on-scroll-to-end';

type Props = {
  onScrollToEnd: () => void
}

export const ArtworkList = ({ children, onScrollToEnd }: PropsWithChildren<Props>) => {
  const { containerRef: artworkListRef, handleScroll } = useOnScrollToEnd<HTMLUListElement>({ onScrollToEnd })

  return (
    <StyledArtworkList ref={artworkListRef} onScroll={handleScroll}>
      {children}
    </StyledArtworkList>
  )
}