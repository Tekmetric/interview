import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { PageTitle } from '@/components/PageTitle/PageTitle'

describe('PageTitle', () => {
  it('renders title in a level-1 heading', () => {
    render(<PageTitle title="Repair Orders" />)

    const heading = screen.getByRole('heading', { level: 1, name: 'Repair Orders' })
    expect(heading).toBeInTheDocument()
    expect(heading).toHaveClass('text-2xl', 'font-bold')
  })
})
