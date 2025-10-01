/* eslint-disable no-undef */
import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import { KanbanCard } from '../kanban-card'
import type { RepairOrder } from '@shared/types'

// Mock @dnd-kit/sortable
vi.mock('@dnd-kit/sortable', () => ({
  useSortable: () => ({
    attributes: {},
    listeners: {},
    setNodeRef: vi.fn(),
    transform: null,
    transition: null,
    isDragging: false,
  }),
}))

// Mock @dnd-kit/utilities
vi.mock('@dnd-kit/utilities', () => ({
  CSS: {
    Transform: {
      toString: () => '',
    },
  },
}))

describe('KanbanCard', () => {
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
    services: ['Oil Change', 'Tire Rotation'],
    assignedTech: null,
    priority: 'NORMAL',
    estimatedDuration: null,
    estimatedCost: null,
    dueTime: null,
    notes: '',
    approvedByCustomer: false,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  }

  it('should render order ID', () => {
    render(<KanbanCard order={mockOrder} />)
    expect(screen.getByText('RO-123')).toBeInTheDocument()
  })

  it('should render vehicle information', () => {
    render(<KanbanCard order={mockOrder} />)
    expect(screen.getByText('2020 Toyota Camry')).toBeInTheDocument()
  })

  it('should render customer name', () => {
    render(<KanbanCard order={mockOrder} />)
    expect(screen.getByText('John Doe')).toBeInTheDocument()
  })

  it('should render HIGH priority badge when priority is HIGH', () => {
    const highPriorityOrder = { ...mockOrder, priority: 'HIGH' as const }
    render(<KanbanCard order={highPriorityOrder} />)

    expect(screen.getByText('HIGH')).toBeInTheDocument()
  })

  it('should not render priority badge when priority is NORMAL', () => {
    render(<KanbanCard order={mockOrder} />)

    expect(screen.queryByText('HIGH')).not.toBeInTheDocument()
  })

  it('should render assigned technician when present', () => {
    const orderWithTech = {
      ...mockOrder,
      assignedTech: {
        id: 'tech-1',
        name: 'Jane Smith',
        initials: 'JS',
        specialties: [],
        active: true,
      },
    }
    render(<KanbanCard order={orderWithTech} />)

    expect(screen.getByText('Jane Smith')).toBeInTheDocument()
  })

  it('should not render technician section when not assigned', () => {
    render(<KanbanCard order={mockOrder} />)

    expect(screen.queryByText(/technician/i)).not.toBeInTheDocument()
  })

  it('should render due time when present', () => {
    const orderWithDueTime = {
      ...mockOrder,
      dueTime: '2024-01-15T14:30:00Z',
    }
    render(<KanbanCard order={orderWithDueTime} />)

    expect(screen.getByText(/Due:/)).toBeInTheDocument()
  })

  it('should not render due time when not present', () => {
    render(<KanbanCard order={mockOrder} />)

    expect(screen.queryByText(/Due:/)).not.toBeInTheDocument()
  })

  it('should render first 2 services', () => {
    render(<KanbanCard order={mockOrder} />)

    expect(screen.getByText('Oil Change')).toBeInTheDocument()
    expect(screen.getByText('Tire Rotation')).toBeInTheDocument()
  })

  it('should show overflow indicator for more than 2 services', () => {
    const orderWithManyServices = {
      ...mockOrder,
      services: ['Oil Change', 'Tire Rotation', 'Brake Service', 'Engine Repair'],
    }
    render(<KanbanCard order={orderWithManyServices} />)

    expect(screen.getByText('Oil Change')).toBeInTheDocument()
    expect(screen.getByText('Tire Rotation')).toBeInTheDocument()
    expect(screen.getByText('+2')).toBeInTheDocument()
    expect(screen.queryByText('Brake Service')).not.toBeInTheDocument()
  })

  it('should not show overflow indicator for 2 or fewer services', () => {
    render(<KanbanCard order={mockOrder} />)

    expect(screen.queryByText(/\+\d+/)).not.toBeInTheDocument()
  })

  it('should have cursor-move class for drag functionality', () => {
    const { container } = render(<KanbanCard order={mockOrder} />)

    const card = container.firstChild as HTMLElement
    expect(card).toHaveClass('cursor-move')
  })

  it('should apply correct priority badge styling for HIGH priority', () => {
    const highPriorityOrder = { ...mockOrder, priority: 'HIGH' as const }
    render(<KanbanCard order={highPriorityOrder} />)

    const badge = screen.getByText('HIGH')
    expect(badge).toHaveClass('border-red-500', 'text-red-700')
  })
})
