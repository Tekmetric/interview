import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { BrandMark } from '@/components/BrandMark/BrandMark'

describe('BrandMark', () => {
  it('renders the TekShop text by default', () => {
    render(<BrandMark />)

    expect(screen.getByText('TekShop')).toBeInTheDocument()
  })

  it('hides text when showText is false', () => {
    render(<BrandMark showText={false} />)

    expect(screen.queryByText('TekShop')).not.toBeInTheDocument()
  })

  it('applies custom root and text classes', () => {
    const { container } = render(<BrandMark className="custom-root" textClassName="custom-text" />)

    const root = container.firstElementChild
    expect(root).toHaveClass('custom-root')
    expect(screen.getByText('TekShop')).toHaveClass('custom-text')
  })
})
