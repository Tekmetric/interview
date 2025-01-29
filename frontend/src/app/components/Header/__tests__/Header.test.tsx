import { render, screen, fireEvent, within } from '@testing-library/react'
import '@testing-library/jest-dom'
import Header from '../Header'
import { describe, it, expect, jest, beforeEach } from '@jest/globals'

jest.mock('next/image', () => ({
  __esModule: true,
  default: (props: any) => <img alt="" {...props} />,
}))

describe('Header', () => {
  beforeEach(() => {
    document.body.innerHTML = ''
  })

  it('renders the SpaceX logo and navigation items', () => {
    render(<Header />)

    expect(screen.getByTestId('spacex-logo')).toBeInTheDocument()

    const mainNav = screen.getByTestId('main-navigation')
    expect(within(mainNav).getByText('Next Launch')).toBeInTheDocument()
    expect(within(mainNav).getByText('Latest Launch')).toBeInTheDocument()
    expect(within(mainNav).getByText('Launches')).toBeInTheDocument()
    expect(within(mainNav).getByText('Rockets')).toBeInTheDocument()
  })

  it('toggles mobile menu when menu button is clicked', () => {
    render(<Header />)

    const menuButton = screen.getByTestId('menu-toggle-button')
    const mobileMenu = screen.getByTestId('mobile-menu')

    expect(mobileMenu).toHaveClass('max-h-0 opacity-0')

    fireEvent.click(menuButton)
    expect(mobileMenu).toHaveClass('max-h-64 opacity-100')

    fireEvent.click(menuButton)
    expect(mobileMenu).toHaveClass('max-h-0 opacity-0')
  })
})
