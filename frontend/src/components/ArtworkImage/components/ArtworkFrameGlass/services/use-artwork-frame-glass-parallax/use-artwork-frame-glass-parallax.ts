import { useCallback, useEffect, useRef } from 'react'

import { getArtworkFrameGlassParallaxOffset } from '../get-artwork-frame-glass-parallax-offset/get-artwork-frame-glass-parallax-offset'
import { getScrollableParent } from '../get-scrollable-parent/get-scrollable-parent'
import { useOnScreen } from '../use-on-screen/use-on-screen'

export const useArtworkFrameGlassParallax = () => {
  const glassRef = useRef<HTMLDivElement>(null)

  const { isIntersecting } = useOnScreen(glassRef)

  const handleGlassParallax = useCallback(() => {
    if (!glassRef.current || !isIntersecting) {
      return
    }

    const glassParallaxOffset = getArtworkFrameGlassParallaxOffset(
      glassRef.current.getBoundingClientRect()
    )

    glassRef.current.style.backgroundPosition = `calc(50% - ${glassParallaxOffset}px) 0px`
  }, [glassRef, isIntersecting])

  const onScroll = useCallback(
    () => requestAnimationFrame(handleGlassParallax),
    [handleGlassParallax]
  )

  useEffect(
    function handleInitialParallaxPosition() {
      if (!isIntersecting) {
        return
      }

      onScroll()
    },
    [isIntersecting, onScroll]
  )

  useEffect(
    function setupParallaxEventListeners() {
      const scrollableParent = getScrollableParent(glassRef.current)

      scrollableParent?.addEventListener('scroll', onScroll)

      return () => {
        scrollableParent?.removeEventListener('scroll', onScroll)
      }
    },
    [onScroll]
  )

  return { glassRef }
}
