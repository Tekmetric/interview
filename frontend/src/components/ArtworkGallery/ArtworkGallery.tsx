import { PropsWithChildren } from 'react'

import { useOnScrollToEnd } from './services/use-on-scroll-to-end/use-on-scroll-to-end'
import { StyledArtworkGallery } from './styles'

type Props = {
  onScrollToEnd: () => void
}

export const ArtworkGallery = ({
  children,
  onScrollToEnd
}: PropsWithChildren<Props>) => {
  const { containerRef: artworkListRef, handleScroll } =
    useOnScrollToEnd<HTMLUListElement>({ onScrollToEnd })

  return (
    <StyledArtworkGallery ref={artworkListRef} onScroll={handleScroll}>
      {children}
    </StyledArtworkGallery>
  )
}
