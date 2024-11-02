import { useCallback, useEffect, useRef, useState } from 'react'

import { getScrollableParent } from '../get-scrollable-parent/get-scrollable-parent'
import { useOnScreen } from '../use-on-screen/use-on-screen'

export const useArtworkFrameGlassParallax = () => {
  const glassRef = useRef<HTMLDivElement>(null)
  const [horizontalOffset, setHorizontalOffset] = useState(0)

  const { isIntersecting } = useOnScreen(glassRef)

  const handleGlassParallax = useCallback(() => {
    if (!glassRef.current || !isIntersecting) {
      return
    }

    const glassRect = glassRef.current.getBoundingClientRect()
    const viewportCenter = window.innerWidth / 2
    const glassCenter = glassRect.left + glassRect.width / 2

    const parallaxIntensity = 0.2

    const frameOffset = (glassCenter - viewportCenter) * parallaxIntensity

    setHorizontalOffset(frameOffset)
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

  return { glassRef, horizontalOffset }
}
