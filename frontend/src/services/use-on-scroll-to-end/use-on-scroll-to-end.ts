import { useRef } from 'react';
import { useDebouncedCallback } from 'use-debounce';

const debounceIntervalMs = 250

export const useOnScrollToEnd = <TElement extends HTMLElement>({ onScrollToEnd }: {
  onScrollToEnd: () => void
}) => {
  const containerRef = useRef<TElement>(null)

  const handleScroll = useDebouncedCallback(
    () => {
      if (!containerRef.current) {
        return
      }

      const clientWidth = containerRef.current.clientWidth

      const scrollLeft = containerRef.current.scrollLeft
      const scrollRight = scrollLeft + clientWidth

      const scrollWidth = containerRef.current.scrollWidth

      const isScrollToEnd = scrollRight === scrollWidth

      if (!isScrollToEnd) {
        return;
      }

      onScrollToEnd()
    },
    debounceIntervalMs
  )

  return {
    containerRef,
    handleScroll
  }
}