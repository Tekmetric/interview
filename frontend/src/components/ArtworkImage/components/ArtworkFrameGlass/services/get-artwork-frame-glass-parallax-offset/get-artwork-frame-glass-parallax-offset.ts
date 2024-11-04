export const getArtworkFrameGlassParallaxOffset = (glassRect: DOMRect) => {
  const viewportCenter = window.innerWidth / 2
  const glassCenter = glassRect.left + glassRect.width / 2

  const parallaxIntensity = 0.2

  return (glassCenter - viewportCenter) * parallaxIntensity
}
