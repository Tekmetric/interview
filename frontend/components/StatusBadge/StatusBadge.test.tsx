import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { StatusBadge } from '@/components/StatusBadge/StatusBadge'

const cases = [
  {
    status: 'new' as const,
    label: 'New',
    className: 'bg-slate-100 text-slate-600',
    dotClass: 'bg-slate-400',
  },
  {
    status: 'in_progress' as const,
    label: 'In Progress',
    className: 'bg-brand-100 text-brand-700',
    dotClass: 'bg-brand',
  },
  {
    status: 'waiting_parts' as const,
    label: 'Waiting Parts',
    className: 'bg-amber-100 text-amber-800',
    dotClass: 'bg-amber-500',
  },
  {
    status: 'completed' as const,
    label: 'Completed',
    className: 'bg-green-100 text-green-700',
    dotClass: 'bg-green-500',
  },
]

describe('StatusBadge', () => {
  it.each(cases)(
    'renders the $label variant for status $status',
    ({ status, label, className, dotClass }) => {
      const { container } = render(<StatusBadge status={status} />)

      const badge = screen.getByText(label)
      expect(badge).toBeInTheDocument()
      expect(badge).toHaveClass(...className.split(' '))

      const dot = container.querySelector('span > span')
      expect(dot).toBeInTheDocument()
      expect(dot).toHaveClass(...dotClass.split(' '))
    },
  )
})
