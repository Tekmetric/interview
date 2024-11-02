import { RefObject, useEffect, useMemo, useState } from 'react'

export const useOnScreen = <T extends HTMLElement = HTMLElement>(
  ref: RefObject<T>
) => {
  const [isIntersecting, setIntersecting] = useState(false)

  const observer = useMemo(() => {
    return new IntersectionObserver(([entry]) => {
      setIntersecting(entry.isIntersecting)
    })
  }, [])

  useEffect(() => {
    const currentRef = ref.current
    if (currentRef) {
      observer.observe(currentRef)
    }

    return () => {
      if (currentRef) {
        observer.unobserve(currentRef)
      }
      observer.disconnect()
    }
  }, [observer, ref])

  return { isIntersecting }
}
