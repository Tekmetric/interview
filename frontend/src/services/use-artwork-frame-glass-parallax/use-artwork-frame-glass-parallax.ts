import { useCallback, useEffect, useRef } from 'react';
import { useOnScreen } from '../use-on-screen/use-on-screen';

const getScrollableParent = (node: HTMLElement | null): HTMLElement | null => {
  if (!node) {
    return null;
  }

  const overflowY = window.getComputedStyle(node).overflowY;

  if (overflowY === 'auto' || overflowY === 'scroll') {
    return node;
  }

  return getScrollableParent(node.parentElement);
};

export const useArtworkFrameGlassParallax = () => {
  const glassRef = useRef<HTMLDivElement>(null);

  const { isIntersecting } = useOnScreen({ ref: glassRef });

  const handleGlassParallax = useCallback(() => {
    if (!glassRef.current || !isIntersecting) {
      return;
    }

    const glassRect = glassRef.current.getBoundingClientRect();
    const viewportCenter = window.innerWidth / 2;
    const glassCenter = glassRect.left + glassRect.width / 2;

    const parallaxIntensity = 0.2;

    const frameOffset = (glassCenter - viewportCenter) * parallaxIntensity;
    
    glassRef.current.style.backgroundPosition = `calc(50% - ${frameOffset}px) 0px`;
  }, [glassRef, isIntersecting]);

  const onScroll = useCallback(
    () => requestAnimationFrame(handleGlassParallax),
    [handleGlassParallax]
  )

  useEffect(function handleInitialParallaxPosition () {
    if (!isIntersecting) {
      return
    }

    onScroll()
  }, [isIntersecting]);

  useEffect(function setupParallaxEventListeners () {
    const scrollableParent = getScrollableParent(glassRef.current);

    scrollableParent?.addEventListener('scroll', onScroll);

    return () => {
      scrollableParent?.removeEventListener('scroll', onScroll);
    };
  }, [handleGlassParallax]);

  return { glassRef }
}
