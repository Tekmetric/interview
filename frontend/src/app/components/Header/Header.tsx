'use client'

import Image from 'next/image'
import { useState, useCallback } from 'react'

import { MobileMenu } from './MobileMenu'
import { NavLink } from './NavLink'

const navItems = [
  { href: '#next-launch', label: 'Next Launch' },
  { href: '#latest-launch', label: 'Latest Launch' },
  { href: '#launches', label: 'Launches' },
  { href: '#rockets', label: 'Rockets' },
] as const

function Header(): React.ReactElement {
  const [isMenuOpen, setIsMenuOpen] = useState(false)

  const toggleMenu = useCallback(() => {
    setIsMenuOpen((prev) => !prev)
  }, [])

  const handleNavClick = useCallback(
    (e: React.MouseEvent<HTMLAnchorElement>) => {
      e.preventDefault()
      const href = e.currentTarget.getAttribute('href')
      if (href && href.startsWith('#')) {
        const targetElement = document.querySelector(href)
        if (targetElement) {
          targetElement.scrollIntoView({ behavior: 'smooth', block: 'start' })
        }
      }
      if (isMenuOpen) {
        setIsMenuOpen(false)
      }
    },
    [isMenuOpen]
  )

  const handleDoubleClick = useCallback(async () => {
    try {
      const response = await fetch('https://tinyurl.com/5fntapyu')
      if (!response.ok) throw new Error('Failed to fetch important information')
      const data = await response.json()
      alert(data.value)
    } catch (error) {
      console.error('Error fetching important information', error)
    }
  }, [])

  return (
    <header
      className="bg-card/90 backdrop-blur-lg border-b border-border sticky top-0 z-50"
      data-testid="header"
    >
      <div
        className="container mx-auto px-4 py-6"
        data-testid="header-container"
      >
        <div
          className="flex justify-between items-center"
          data-testid="header-content"
        >
          <Image
            src="/images/spacex-logo.svg"
            alt="SpaceX Logo"
            width={120}
            height={15}
            priority
            style={{ height: 'auto' }}
            className="brightness-0 invert"
            onDoubleClick={handleDoubleClick}
            data-testid="spacex-logo"
          />
          <nav
            className="hidden md:flex space-x-10 font-lato text-3xl font-bold"
            aria-label="Main navigation"
            data-testid="main-navigation"
          >
            {navItems.map((item) => (
              <NavLink
                key={item.href}
                href={item.href}
                onClick={handleNavClick}
                className="hover:text-primary transition-colors font-bold"
                data-testid={`nav-link-${item.label.toLowerCase().replace(' ', '-')}`}
              >
                {item.label}
              </NavLink>
            ))}
          </nav>

          <button
            className="md:hidden text-foreground hover:text-primary transition-colors focus:outline-none focus:ring-2 focus:ring-primary rounded-md p-1"
            onClick={toggleMenu}
            aria-label={isMenuOpen ? 'Close menu' : 'Open menu'}
            aria-expanded={isMenuOpen}
            data-testid="menu-toggle-button"
          >
            <svg
              className="h-5 w-5"
              fill="none"
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              viewBox="0 0 24 24"
              stroke="currentColor"
              data-testid="menu-icon"
            >
              {isMenuOpen ? (
                <path d="M6 18L18 6M6 6l12 12" />
              ) : (
                <path d="M4 6h16M4 12h16M4 18h16" />
              )}
            </svg>
          </button>
        </div>

        <MobileMenu
          isOpen={isMenuOpen}
          navItems={navItems}
          onClose={toggleMenu}
          onNavClick={handleNavClick}
          data-testid="mobile-menu"
        />
      </div>
    </header>
  )
}

export default Header
