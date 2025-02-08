import { render, screen, fireEvent } from '@testing-library/react'
import '@testing-library/jest-dom'
import { NavLink } from '../NavLink'

describe('NavLink', () => {
  it('renders with the correct href and children', () => {
    render(<NavLink href="/home">Home</NavLink>)
    const link = screen.getByTestId('nav-link-/home')
    expect(link).toBeInTheDocument()
    expect(link).toHaveAttribute('href', '/home')
    expect(link).toHaveTextContent('Home')
  })

  it('applies custom className', () => {
    render(
      <NavLink href="/about" className="custom-class">
        About
      </NavLink>
    )
    const link = screen.getByTestId('nav-link-/about')
    expect(link).toHaveClass('custom-class')
  })

  it('calls onClick handler when clicked', () => {
    const onClick = jest.fn()
    render(
      <NavLink href="/click" onClick={onClick}>
        Click me
      </NavLink>
    )
    const link = screen.getByTestId('nav-link-/click')
    fireEvent.click(link)
    expect(onClick).toHaveBeenCalledTimes(1)
  })

  it("generates the correct data-testid for href with '#' and spaces", () => {
    render(<NavLink href="#About Us">About Us</NavLink>)
    const link = screen.getByTestId('nav-link-about-us')
    expect(link).toBeInTheDocument()
    expect(link).toHaveAttribute('href', '#About Us')
  })
})
