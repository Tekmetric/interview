/* eslint-disable no-undef */
import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import { KanbanCard } from '../kanban-card'
import type { RepairOrder } from '@shared/types'
import { PreferencesContext } from '@/contexts/preferences-context'
import { DEFAULT_PREFERENCES } from '@/types/preferences'
import { SelectionProvider } from '@/contexts/selection-context'

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

const customRender = (ui: React.ReactElement, options?: any) => {
  return render(
    <PreferencesContext.Provider
      value={{
        preferences: { ...DEFAULT_PREFERENCES, savedFilters: [] },
        updateColumnVisibility: vi.fn(),
        saveFilterPreset: vi.fn(),
        deleteFilterPreset: vi.fn(),
        setDefaultPreset: vi.fn(),
        getPresetById: vi.fn(),
      }}
    >
      <SelectionProvider>{ui}</SelectionProvider>
    </PreferencesContext.Provider>,
    options,
  )
}

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
    estimatedDuration: undefined,
    estimatedCost: undefined,
    dueTime: undefined,
    notes: '',
    approvedByCustomer: false,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  }

  it('should render order ID', () => {
    customRender(<KanbanCard order={mockOrder} />)
    expect(screen.getByText('RO-123')).toBeInTheDocument()
  })

  it('should render vehicle information', () => {
    customRender(<KanbanCard order={mockOrder} />)
    expect(screen.getByText('2020 Toyota Camry')).toBeInTheDocument()
  })

  it('should render customer name', () => {
    customRender(<KanbanCard order={mockOrder} />)
    expect(screen.getByText('John Doe')).toBeInTheDocument()
  })

  it('should render HIGH priority badge when priority is HIGH', () => {
    const highPriorityOrder = { ...mockOrder, priority: 'HIGH' as const }
    customRender(<KanbanCard order={highPriorityOrder} />)

    const priorityIcon = screen.getByLabelText('High Priority');
    expect(priorityIcon).toBeInTheDocument();
  })

  it('should not render priority badge when priority is NORMAL', () => {
    customRender(<KanbanCard order={mockOrder} />)

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
    customRender(<KanbanCard order={orderWithTech} />)

    expect(screen.getByText('Jane Smith')).toBeInTheDocument()
  })

  it('should not render technician section when not assigned', () => {
    customRender(<KanbanCard order={mockOrder} />)

    expect(screen.queryByText(/technician/i)).not.toBeInTheDocument()
  })

  it('should render due time when present', () => {
    const orderWithDueTime = {
      ...mockOrder,
      dueTime: '2024-01-15T14:30:00Z',
    }
    customRender(<KanbanCard order={orderWithDueTime} />)

    expect(screen.getByText(/Due/)).toBeInTheDocument()
  })

  it('should not render due time when not present', () => {
    customRender(<KanbanCard order={mockOrder} />)

    expect(screen.queryByText(/Due:/)).not.toBeInTheDocument()
  })

  it('should render first 2 services', () => {
    customRender(<KanbanCard order={mockOrder} />)

    expect(screen.getByText('Oil Change')).toBeInTheDocument()
    expect(screen.getByText('Tire Rotation')).toBeInTheDocument()
  })

  it('should show overflow indicator for more than 2 services', () => {
    const orderWithManyServices = {
      ...mockOrder,
      services: ['Oil Change', 'Tire Rotation', 'Brake Service', 'Engine Repair'],
    }
    customRender(<KanbanCard order={orderWithManyServices} />)

    expect(screen.getByText('Oil Change')).toBeInTheDocument()
    expect(screen.getByText('Tire Rotation')).toBeInTheDocument()
    expect(screen.getByText('+2')).toBeInTheDocument()
    expect(screen.queryByText('Brake Service')).not.toBeInTheDocument()
  })

  it('should not show overflow indicator for 2 or fewer services', () => {
    customRender(<KanbanCard order={mockOrder} />)

    expect(screen.queryByText(/\+\d+/)).not.toBeInTheDocument()
  })

  it('should have cursor-move class for drag functionality', () => {
    const { container } = customRender(<KanbanCard order={mockOrder} />)

    const card = container.querySelector('.cursor-move')
    expect(card).toBeInTheDocument()
  })


})
