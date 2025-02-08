'use client'

import { useEffect } from 'react'

export default function SmoothScroll(): null {
  useEffect(() => {
    const HEADER_HEIGHT = 64

    const calculateOffset = (): number => {
      const header = document.querySelector('header')
      const mobileMenu = document.querySelector('.mobile-menu')
      const headerHeight = header
        ? header.getBoundingClientRect().height
        : HEADER_HEIGHT
      const mobileMenuHeight = mobileMenu
        ? mobileMenu.getBoundingClientRect().height
        : 0
      return headerHeight + mobileMenuHeight
    }

    const handleClick = (e: MouseEvent): void => {
      const target = e.target as HTMLAnchorElement
      if (target.hash) {
        e.preventDefault()
        const element = document.querySelector(target.hash)
        if (element) {
          const offset = calculateOffset()
          const elementPosition = element.getBoundingClientRect().top
          const offsetPosition = elementPosition + window.scrollY - offset

          window.scrollTo({
            top: offsetPosition,
            behavior: 'smooth',
          })
        }
      }
    }

    document.addEventListener('click', handleClick)

    return () => {
      document.removeEventListener('click', handleClick)
    }
  }, [])

  return null
}
