/* eslint-disable no-undef */
import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import { userEvent } from '@testing-library/user-event'
import { KPICard } from '../kpi-card'

describe('KPICard', () => {
  const mockIcon = <svg data-testid='test-icon'>Icon</svg>

  it('should render title and value', () => {
    render(<KPICard title='Total WIP' value={42} icon={mockIcon} />)

    expect(screen.getByText('Total WIP')).toBeInTheDocument()
    expect(screen.getByText('42')).toBeInTheDocument()
  })

  it('should render icon', () => {
    render(<KPICard title='Total WIP' value={42} icon={mockIcon} />)

    expect(screen.getByTestId('test-icon')).toBeInTheDocument()
  })

  it('should apply default variant styles', () => {
    const { container } = render(<KPICard title='Total WIP' value={42} icon={mockIcon} />)

    const card = container.firstChild as HTMLElement
    expect(card).toHaveClass('bg-white', 'border-gray-200')
  })

  it('should apply primary variant styles', () => {
    const { container } = render(
      <KPICard title='Total WIP' value={42} icon={mockIcon} variant='primary' />,
    )

    const card = container.firstChild as HTMLElement
    expect(card).toHaveClass('bg-blue-50', 'border-blue-200')
  })

  it('should apply warning variant styles', () => {
    const { container } = render(
      <KPICard title='Overdue' value={5} icon={mockIcon} variant='warning' />,
    )

    const card = container.firstChild as HTMLElement
    expect(card).toHaveClass('bg-amber-50', 'border-amber-200')
  })

  it('should apply info variant styles', () => {
    const { container } = render(
      <KPICard title='Info' value={10} icon={mockIcon} variant='info' />,
    )

    const card = container.firstChild as HTMLElement
    expect(card).toHaveClass('bg-purple-50', 'border-purple-200')
  })

  it('should apply success variant styles', () => {
    const { container } = render(
      <KPICard title='Success' value={15} icon={mockIcon} variant='success' />,
    )

    const card = container.firstChild as HTMLElement
    expect(card).toHaveClass('bg-green-50', 'border-green-200')
  })

  it('should call onClick when clicked', async () => {
    const user = userEvent.setup()
    const onClick = vi.fn()

    render(<KPICard title='Total WIP' value={42} icon={mockIcon} onClick={onClick} />)

    const card = screen.getByText('Total WIP').closest('div')?.parentElement
    if (card) {
      await user.click(card)
      expect(onClick).toHaveBeenCalledTimes(1)
    }
  })

  it('should apply cursor-pointer class when onClick is provided', () => {
    const onClick = vi.fn()
    const { container } = render(
      <KPICard title='Total WIP' value={42} icon={mockIcon} onClick={onClick} />,
    )

    const card = container.firstChild as HTMLElement
    expect(card).toHaveClass('cursor-pointer')
  })

  it('should not apply cursor-pointer class when onClick is not provided', () => {
    const { container } = render(<KPICard title='Total WIP' value={42} icon={mockIcon} />)

    const card = container.firstChild as HTMLElement
    expect(card).not.toHaveClass('cursor-pointer')
  })
})
