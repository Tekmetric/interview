import { render, screen, fireEvent } from '@testing-library/react'
import '@testing-library/jest-dom'
import { MobileMenu } from '../MobileMenu'

describe('MobileMenu', () => {
  const navItems = [
    { href: '/home', label: 'Home' },
    { href: '/about', label: 'About' },
  ]

  const onClose = jest.fn()
  const onNavClick = jest.fn()

  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('renders with closed state when isOpen is false', () => {
    render(
      <MobileMenu
        isOpen={false}
        navItems={navItems}
        onClose={onClose}
        onNavClick={onNavClick}
      />
    )
    const menu = screen.getByTestId('mobile-menu')
    expect(menu).toHaveClass('max-h-0')
    expect(menu).toHaveClass('opacity-0')
  })

  it('renders with open state when isOpen is true', () => {
    render(
      <MobileMenu
        isOpen={true}
        navItems={navItems}
        onClose={onClose}
        onNavClick={onNavClick}
      />
    )
    const menu = screen.getByTestId('mobile-menu')
    expect(menu).toHaveClass('max-h-64')
    expect(menu).toHaveClass('opacity-100')
  })

  it('renders the correct number of nav items with proper attributes', () => {
    render(
      <MobileMenu
        isOpen={true}
        navItems={navItems}
        onClose={onClose}
        onNavClick={onNavClick}
      />
    )
    navItems.forEach((item) => {
      const testId = `nav-link-${item.href}`
      const link = screen.getByTestId(testId)
      expect(link).toBeInTheDocument()
      expect(link).toHaveAttribute('href', item.href)
    })
  })

  it('calls onNavClick and onClose when a nav link is clicked', () => {
    render(
      <MobileMenu
        isOpen={true}
        navItems={navItems}
        onClose={onClose}
        onNavClick={onNavClick}
      />
    )
    const testId = `nav-link-${navItems[0].href}`
    const link = screen.getByTestId(testId)
    fireEvent.click(link)
    expect(onNavClick).toHaveBeenCalledTimes(1)
    expect(onClose).toHaveBeenCalledTimes(1)
  })
})
