import { useArtworkFrameGlassParallax } from './services/use-artwork-frame-glass-parallax/use-artwork-frame-glass-parallax'
import { StyledArtworkFrameGlass } from './styled'

export const ArtworkFrameGlass = () => {
  const { glassRef } = useArtworkFrameGlassParallax()

  return <StyledArtworkFrameGlass ref={glassRef} />
}
