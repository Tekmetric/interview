import { StyledArtworkFrameGlass } from './styled';
import {
  useArtworkFrameGlassParallax
} from './services/use-artwork-frame-glass-parallax/use-artwork-frame-glass-parallax';


export const ArtworkFrameGlass = () => {
  const { glassRef } = useArtworkFrameGlassParallax();

  return <StyledArtworkFrameGlass ref={glassRef} />;
};