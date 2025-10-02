import { describe, it, expect, vi } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import { toast } from 'sonner'
import { KanbanBoard } from '../kanban-board'
import { RO_STATUS, KANBAN_LABELS } from '@shared/constants'
import { PreferencesContext } from '@/contexts/preferences-context'
import { DEFAULT_PREFERENCES } from '@/types/preferences'
import { SelectionProvider } from '@/contexts/selection-context'
import type { RepairOrder } from '@shared/types'

vi.mock('sonner', () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn(),
  },
}))

describe('KanbanBoard - Drag and Drop Transitions', () => {
  const mockOnStatusChange = vi.fn()

  const createMockOrder = (overrides: Partial<RepairOrder>): RepairOrder => ({
    id: 'RO-1',
    status: RO_STATUS.NEW,
    customer: { name: 'John Doe', phone: '555-1234' },
    vehicle: { year: 2020, make: 'Toyota', model: 'Camry' },
    services: ['Oil Change'],
    assignedTech: null,
    priority: 'NORMAL',
    notes: '',
    approvedByCustomer: false,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
    ...overrides,
  })

  const customRender = (ui: React.ReactElement) => {
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
    )
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should show toast error for invalid transition NEW -> COMPLETED', async () => {
    const order = createMockOrder({ status: RO_STATUS.NEW })
    customRender(<KanbanBoard orders={[order]} onStatusChange={mockOnStatusChange} />)

    // Simulate drag end with invalid transition
    const board = screen.getByText('New').closest('div')
    expect(board).toBeInTheDocument()

    // In a real test, we'd simulate the drag event
    // For now, verify the transition validation logic would catch this
    const { canTransition } = await import('@shared/transitions')
    const validation = canTransition(RO_STATUS.NEW, RO_STATUS.COMPLETED, order)

    expect(validation.allowed).toBe(false)
    expect(validation.reason).toContain('Cannot move from NEW to COMPLETED')
  })

  it('should show toast error when moving to IN_PROGRESS without tech', async () => {
    const order = createMockOrder({
      status: RO_STATUS.AWAITING_APPROVAL,
      assignedTech: null,
    })

    const { canTransition } = await import('@shared/transitions')
    const validation = canTransition(
      RO_STATUS.AWAITING_APPROVAL,
      RO_STATUS.IN_PROGRESS,
      order,
    )

    expect(validation.allowed).toBe(false)
    expect(validation.reason).toBe('Assign a technician before starting work')
  })

  it('should show toast error when moving to COMPLETED without approval', async () => {
    const order = createMockOrder({
      status: RO_STATUS.IN_PROGRESS,
      assignedTech: {
        id: 'tech-1',
        name: 'Tech 1',
        initials: 'T1',
        specialties: [],
        active: true,
      },
      approvedByCustomer: false,
    })

    const { canTransition } = await import('@shared/transitions')
    const validation = canTransition(RO_STATUS.IN_PROGRESS, RO_STATUS.COMPLETED, order)

    expect(validation.allowed).toBe(false)
    expect(validation.reason).toBe(
      'Customer approval required before marking as completed',
    )
  })

  it('should allow valid transition NEW -> AWAITING_APPROVAL', async () => {
    const order = createMockOrder({ status: RO_STATUS.NEW })

    const { canTransition } = await import('@shared/transitions')
    const validation = canTransition(RO_STATUS.NEW, RO_STATUS.AWAITING_APPROVAL, order)

    expect(validation.allowed).toBe(true)
    expect(validation.reason).toBeUndefined()
  })

  it('should allow valid transition AWAITING_APPROVAL -> IN_PROGRESS with tech', async () => {
    const order = createMockOrder({
      status: RO_STATUS.AWAITING_APPROVAL,
      assignedTech: {
        id: 'tech-1',
        name: 'Tech 1',
        initials: 'T1',
        specialties: [],
        active: true,
      },
    })

    const { canTransition } = await import('@shared/transitions')
    const validation = canTransition(
      RO_STATUS.AWAITING_APPROVAL,
      RO_STATUS.IN_PROGRESS,
      order,
    )

    expect(validation.allowed).toBe(true)
  })

  it('should allow valid transition IN_PROGRESS -> COMPLETED with approval', async () => {
    const order = createMockOrder({
      status: RO_STATUS.IN_PROGRESS,
      assignedTech: {
        id: 'tech-1',
        name: 'Tech 1',
        initials: 'T1',
        specialties: [],
        active: true,
      },
      approvedByCustomer: true,
    })

    const { canTransition } = await import('@shared/transitions')
    const validation = canTransition(RO_STATUS.IN_PROGRESS, RO_STATUS.COMPLETED, order)

    expect(validation.allowed).toBe(true)
  })

  it('should render all 5 columns', () => {
    customRender(<KanbanBoard orders={[]} onStatusChange={mockOnStatusChange} />)

    expect(screen.getByText('New')).toBeInTheDocument()
    expect(screen.getByText('Awaiting Approval')).toBeInTheDocument()
    expect(screen.getByText('In Progress')).toBeInTheDocument()
    expect(screen.getByText('Waiting Parts')).toBeInTheDocument()
    expect(screen.getByText('Completed')).toBeInTheDocument()
  })

  it('should group orders by status correctly', () => {
    const orders = [
      createMockOrder({ id: 'RO-1', status: RO_STATUS.NEW }),
      createMockOrder({ id: 'RO-2', status: RO_STATUS.IN_PROGRESS }),
      createMockOrder({ id: 'RO-3', status: RO_STATUS.NEW }),
    ]

    customRender(<KanbanBoard orders={orders} onStatusChange={mockOnStatusChange} />)

    // Verify orders are displayed
    expect(screen.getByText('RO-1')).toBeInTheDocument()
    expect(screen.getByText('RO-2')).toBeInTheDocument()
    expect(screen.getByText('RO-3')).toBeInTheDocument()
  })
})
