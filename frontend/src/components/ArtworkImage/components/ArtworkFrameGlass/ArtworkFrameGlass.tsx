import { useArtworkFrameGlassParallax } from './services/use-artwork-frame-glass-parallax/use-artwork-frame-glass-parallax'
import { StyledArtworkFrameGlass } from './styled'

export const ArtworkFrameGlass = () => {
  const { glassRef, horizontalOffset } = useArtworkFrameGlassParallax()

  return (
    <StyledArtworkFrameGlass
      ref={glassRef}
      $horizontalOffset={horizontalOffset}
    />
  )
}
