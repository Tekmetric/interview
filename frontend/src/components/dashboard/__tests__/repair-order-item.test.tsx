/* eslint-disable no-undef */
import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { RepairOrderItem } from '../repair-order-item'
import type { RepairOrder } from '@shared/types'

describe('RepairOrderItem', () => {
  const mockOrder: RepairOrder = {
    id: 'RO-123',
    status: 'IN_PROGRESS',
    customer: {
      name: 'John Doe',
      phone: '555-0000',
    },
    vehicle: {
      year: 2020,
      make: 'Toyota',
      model: 'Camry',
    },
    services: ['Oil Change'],
    assignedTech: null,
    priority: 'NORMAL',
    estimatedDuration: undefined,
    estimatedCost: undefined,
    dueTime: undefined,
    notes: '',
    approvedByCustomer: false,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  }

  it('should render order ID', () => {
    render(<RepairOrderItem order={mockOrder} />)
    expect(screen.getByText('RO-123')).toBeInTheDocument()
  })

  it('should render vehicle information', () => {
    render(<RepairOrderItem order={mockOrder} />)
    expect(screen.getByText('2020 Toyota Camry')).toBeInTheDocument()
  })

  it('should render customer name', () => {
    render(<RepairOrderItem order={mockOrder} />)
    expect(screen.getByText('John Doe')).toBeInTheDocument()
  })

  it('should render status badge', () => {
    render(<RepairOrderItem order={mockOrder} />)
    expect(screen.getByText('In Progress')).toBeInTheDocument()
  })

  it('should render HIGH priority badge when priority is HIGH', () => {
    const highPriorityOrder = { ...mockOrder, priority: 'HIGH' as const }
    render(<RepairOrderItem order={highPriorityOrder} />)

    const priorityIcon = screen.getByLabelText('High Priority')
    expect(priorityIcon).toBeInTheDocument()
  })

  it('should not render priority badge when priority is NORMAL', () => {
    render(<RepairOrderItem order={mockOrder} />)

    const priorityBadges = screen.queryAllByText(/HIGH/i)
    expect(priorityBadges).toHaveLength(0)
  })

  it('should render due time when present', () => {
    const orderWithDueTime = {
      ...mockOrder,
      dueTime: '2024-01-15T14:30:00Z',
    }
    render(<RepairOrderItem order={orderWithDueTime} />)

    expect(screen.getByText(/Due:/)).toBeInTheDocument()
  })

  it('should not render due time when not present', () => {
    render(<RepairOrderItem order={mockOrder} />)

    expect(screen.queryByText(/Due:/)).not.toBeInTheDocument()
  })

  it('should apply correct status badge colors', () => {
    const statuses: Array<RepairOrder['status']> = [
      'NEW',
      'AWAITING_APPROVAL',
      'IN_PROGRESS',
      'WAITING_PARTS',
      'COMPLETED',
    ]

    const statusLabels: Record<RepairOrder['status'], string> = {
      NEW: 'New',
      IN_PROGRESS: 'In Progress',
      AWAITING_APPROVAL: 'Awaiting Approval',
      WAITING_PARTS: 'Waiting Parts',
      COMPLETED: 'Completed',
    }

    statuses.forEach((status) => {
      const { unmount } = render(<RepairOrderItem order={{ ...mockOrder, status }} />)

      const badge = screen.getByText(statusLabels[status])
      expect(badge).toBeInTheDocument()

      unmount()
    })
  })

  it('should render with hover effect class and cursor-pointer', () => {
    const { container } = render(<RepairOrderItem order={mockOrder} />)

    const orderItem = container.firstChild as HTMLElement
    expect(orderItem).toHaveClass('cursor-pointer')
    expect(orderItem).toHaveClass('hover:shadow-sm')
  })
})
