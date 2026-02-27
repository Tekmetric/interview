import { Activity } from 'lucide-react'
import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { KpiCard } from '@/components/KpiCard/KpiCard'

describe('KpiCard', () => {
  it('renders title, value, and icon', () => {
    const { container } = render(
      <KpiCard
        title="Open Orders"
        value="12"
        icon={<Activity data-testid="kpi-icon" />}
        iconClass="bg-brand-100 text-brand-700"
      />,
    )

    expect(screen.getByText('Open Orders')).toBeInTheDocument()
    expect(screen.getByText('12')).toBeInTheDocument()
    expect(screen.getByTestId('kpi-icon')).toBeInTheDocument()
    expect(container.querySelector('.bg-brand-100')).toBeInTheDocument()
  })

  it('uses the default value class when valueClass is omitted', () => {
    render(
      <KpiCard
        title="Revenue"
        value="$9,221"
        icon={<Activity />}
        iconClass="bg-green-100 text-green-700"
      />,
    )

    expect(screen.getByText('$9,221')).toHaveClass('text-foreground')
  })

  it('uses valueClass when provided', () => {
    render(
      <KpiCard
        title="Completion"
        value="89%"
        icon={<Activity />}
        iconClass="bg-blue-100 text-blue-700"
        valueClass="text-green-600"
      />,
    )

    expect(screen.getByText('89%')).toHaveClass('text-green-600')
  })
})
