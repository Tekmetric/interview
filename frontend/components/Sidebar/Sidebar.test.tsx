import { render, screen } from '@testing-library/react'
import type { ComponentProps } from 'react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { Sidebar } from '@/components/Sidebar/Sidebar'

let mockedPathname = '/dashboard'

vi.mock('next/navigation', () => ({
  usePathname: () => mockedPathname,
}))

vi.mock('next/link', () => ({
  default: ({ href, children, ...props }: ComponentProps<'a'> & { href: string }) => (
    <a href={href} {...props}>
      {children}
    </a>
  ),
}))

describe('Sidebar', () => {
  beforeEach(() => {
    mockedPathname = '/dashboard'
  })

  it('renders all navigation links', () => {
    render(<Sidebar />)

    expect(screen.getByRole('link', { name: 'Dashboard' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'Customers' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'Vehicles' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'Repair Orders' })).toBeInTheDocument()
  })

  it('marks current route as active', () => {
    mockedPathname = '/customers'
    render(<Sidebar />)

    const active = screen.getByRole('link', { name: 'Customers' })
    const inactive = screen.getByRole('link', { name: 'Vehicles' })

    expect(active).toHaveClass('border-brand', 'text-white')
    expect(inactive).toHaveClass('border-transparent')
  })

  it('marks nested route as active for matching section', () => {
    mockedPathname = '/repair-orders/RO-1001'
    render(<Sidebar />)

    expect(screen.getByRole('link', { name: 'Repair Orders' })).toHaveClass(
      'border-brand',
      'text-white',
    )
  })

  it('renders current year in footer', () => {
    render(<Sidebar />)

    expect(screen.getByText(new RegExp(String(new Date().getFullYear())))).toBeInTheDocument()
  })
})
