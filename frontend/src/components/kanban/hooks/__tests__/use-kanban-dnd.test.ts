 
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { useKanbanDndHandlers } from '../use-kanban-dnd-handlers'
import { DragEndEvent, DragOverEvent } from '@dnd-kit/core'
import { RO_STATUS } from '@shared/constants'

vi.mock('sonner', () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn(),
  },
}))

describe('useKanbanDndHandlers', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  const createMockOrders = () => [
    {
      id: '1',
      status: RO_STATUS.NEW,
      customer: { name: 'John Doe', phone: '555-0100' },
      vehicle: { year: 2020, make: 'Toyota', model: 'Camry' },
      services: [],
      approvedByCustomer: true,
      assignedTech: { id: 'tech-1', name: 'Tech 1', initials: 'T1', specialties: [], active: true },
      priority: 'NORMAL' as const,
      notes: '',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z',
    },
    {
      id: '2',
      status: RO_STATUS.IN_PROGRESS,
      customer: { name: 'Jane Smith', phone: '555-0200' },
      vehicle: { year: 2021, make: 'Honda', model: 'Civic' },
      services: [],
      approvedByCustomer: false,
      assignedTech: null,
      priority: 'NORMAL' as const,
      notes: '',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z',
    },
  ]

  it('should call onStatusChange when a card is dropped into a new column', () => {
    const onStatusChange = vi.fn()
    const clearSelection = vi.fn()
    const mockOrders = createMockOrders()
    const { result } = renderHook(() =>
      useKanbanDndHandlers({ orders: mockOrders, onStatusChange, clearSelection }),
    )

    const dragEndEvent: DragEndEvent = {
      active: { id: '1', data: { current: { sortable: { index: 0, items: ['1'] } } }, rect: { current: { initial: null, translated: null } } },
      over: {
        id: RO_STATUS.IN_PROGRESS,
        data: { current: { sortable: { index: 0, items: [] } } },
        rect: { left: 0, top: 0, right: 0, bottom: 0, width: 0, height: 0 },
        disabled: false,
      },
      delta: { x: 0, y: 0 },
      activatorEvent: null as any,
      collisions: null,
    }

    act(() => {
      result.current.handleDragEnd(dragEndEvent)
    })

    expect(onStatusChange).toHaveBeenCalledWith('1', RO_STATUS.IN_PROGRESS)
  })

  it('should not call onStatusChange when a card is dropped into the same column', () => {
    const onStatusChange = vi.fn()
    const clearSelection = vi.fn()
    const mockOrders = createMockOrders()
    const { result } = renderHook(() =>
      useKanbanDndHandlers({ orders: mockOrders, onStatusChange, clearSelection }),
    )

    const dragEndEvent: DragEndEvent = {
      active: { id: '1', data: { current: { sortable: { index: 0, items: ['1'] } } }, rect: { current: { initial: null, translated: null } } },
      over: {
        id: RO_STATUS.NEW,
        data: { current: { sortable: { index: 0, items: [] } } },
        rect: { left: 0, top: 0, right: 0, bottom: 0, width: 0, height: 0 },
        disabled: false,
      },
      delta: { x: 0, y: 0 },
      activatorEvent: null as any,
      collisions: null,
    }

    act(() => {
      result.current.handleDragEnd(dragEndEvent)
    })

    expect(onStatusChange).not.toHaveBeenCalled()
  })

  it('should not call onStatusChange when the transition is not allowed', () => {
    const clearSelection = vi.fn()
    const onStatusChange = vi.fn()
    const mockOrders = createMockOrders()
    const { result } = renderHook(() =>
      useKanbanDndHandlers({ orders: mockOrders, onStatusChange, clearSelection }),
    )

    const dragEndEvent: DragEndEvent = {
      active: { id: '1', data: { current: { sortable: { index: 0, items: ['1'] } } }, rect: { current: { initial: null, translated: null } } },
      over: {
        id: RO_STATUS.COMPLETED,
        data: { current: { sortable: { index: 0, items: [] } } },
        rect: { left: 0, top: 0, right: 0, bottom: 0, width: 0, height: 0 },
        disabled: false,
      },
      delta: { x: 0, y: 0 },
      activatorEvent: null as any,
      collisions: null,
    }

    act(() => {
      result.current.handleDragEnd(dragEndEvent)
    })

    expect(onStatusChange).not.toHaveBeenCalled()
  })

  it('should respect drop position when dragging to a specific card', () => {
    const onStatusChange = vi.fn()
    const clearSelection = vi.fn()
    const orders = [
      {
        id: '1',
        status: RO_STATUS.NEW,
        customer: { name: 'John Doe', phone: '555-0100' },
        vehicle: { year: 2020, make: 'Toyota', model: 'Camry' },
        services: [],
        approvedByCustomer: true,
        assignedTech: { id: 'tech-1', name: 'Tech 1', initials: 'T1', specialties: [], active: true },
        priority: 'NORMAL' as const,
        notes: '',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      },
      {
        id: '2',
        status: RO_STATUS.IN_PROGRESS,
        customer: { name: 'Jane Smith', phone: '555-0200' },
        vehicle: { year: 2021, make: 'Honda', model: 'Civic' },
        services: [],
        approvedByCustomer: true,
        assignedTech: { id: 'tech-1', name: 'Tech 1', initials: 'T1', specialties: [], active: true },
        priority: 'NORMAL' as const,
        notes: '',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      },
      {
        id: '3',
        status: RO_STATUS.IN_PROGRESS,
        customer: { name: 'Bob Johnson', phone: '555-0300' },
        vehicle: { year: 2019, make: 'Ford', model: 'F-150' },
        services: [],
        approvedByCustomer: true,
        assignedTech: { id: 'tech-1', name: 'Tech 1', initials: 'T1', specialties: [], active: true },
        priority: 'NORMAL' as const,
        notes: '',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      },
    ]

    const { result } = renderHook(() =>
      useKanbanDndHandlers({ orders, onStatusChange, clearSelection }),
    )

    // Simulate drag over card '2' with 'top' position
    const dragOverEvent: DragOverEvent = {
      active: {
        id: '1',
        data: { current: {} },
        rect: {
          current: {
            initial: { top: 0, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
            translated: { top: 100, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
          },
        },
      },
      over: {
        id: '2',
        data: { current: {} },
        rect: { top: 200, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
        disabled: false,
      },
      delta: { x: 0, y: 100 },
      collisions: null,
      activatorEvent: null as any,
    }

    act(() => {
      result.current.handleDragOver(dragOverEvent)
    })

    // Drop on card '2'
    const dragEndEvent: DragEndEvent = {
      active: {
        id: '1',
        data: { current: {} },
        rect: {
          current: {
            initial: { top: 0, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
            translated: { top: 100, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
          },
        },
      },
      over: {
        id: '2',
        data: { current: {} },
        rect: { top: 200, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
        disabled: false,
      },
      delta: { x: 0, y: 100 },
      activatorEvent: null as any,
      collisions: null,
    }

    act(() => {
      result.current.handleDragEnd(dragEndEvent)
    })

    // Verify order '1' was inserted before '2' (top position)
    const finalOrders = result.current.localOrders
    const order1Index = finalOrders.findIndex((o) => o.id === '1')
    const order2Index = finalOrders.findIndex((o) => o.id === '2')
    expect(order1Index).toBeLessThan(order2Index)
    expect(onStatusChange).toHaveBeenCalledWith('1', RO_STATUS.IN_PROGRESS)
  })

  it('should use lastOverCardId when drop target is column but card was hovered', () => {
    const onStatusChange = vi.fn()
    const clearSelection = vi.fn()
    const orders = [
      {
        id: '1',
        status: RO_STATUS.NEW,
        customer: { name: 'John Doe', phone: '555-0100' },
        vehicle: { year: 2020, make: 'Toyota', model: 'Camry' },
        services: [],
        approvedByCustomer: true,
        assignedTech: { id: 'tech-1', name: 'Tech 1', initials: 'T1', specialties: [], active: true },
        priority: 'NORMAL' as const,
        notes: '',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      },
      {
        id: '2',
        status: RO_STATUS.IN_PROGRESS,
        customer: { name: 'Jane Smith', phone: '555-0200' },
        vehicle: { year: 2021, make: 'Honda', model: 'Civic' },
        services: [],
        approvedByCustomer: true,
        assignedTech: { id: 'tech-1', name: 'Tech 1', initials: 'T1', specialties: [], active: true },
        priority: 'NORMAL' as const,
        notes: '',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      },
    ]

    const { result } = renderHook(() =>
      useKanbanDndHandlers({ orders, onStatusChange, clearSelection }),
    )

    // Simulate drag over card '2' to establish lastOverCardId
    const dragOverEvent: DragOverEvent = {
      active: {
        id: '1',
        data: { current: {} },
        rect: {
          current: {
            initial: { top: 0, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
            translated: { top: 100, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
          },
        },
      },
      over: {
        id: '2',
        data: { current: {} },
        rect: { top: 200, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
        disabled: false,
      },
      delta: { x: 0, y: 100 },
      collisions: null,
      activatorEvent: null as any,
    }

    act(() => {
      result.current.handleDragOver(dragOverEvent)
    })

    // Drop on column instead of card (simulates over switching to column)
    const dragEndEvent: DragEndEvent = {
      active: {
        id: '1',
        data: { current: {} },
        rect: {
          current: {
            initial: { top: 0, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
            translated: { top: 100, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
          },
        },
      },
      over: {
        id: RO_STATUS.IN_PROGRESS,
        data: { current: {} },
        rect: { top: 200, left: 0, right: 0, bottom: 0, width: 300, height: 600 },
        disabled: false,
      },
      delta: { x: 0, y: 100 },
      activatorEvent: null as any,
      collisions: null,
    }

    act(() => {
      result.current.handleDragEnd(dragEndEvent)
    })

    // Verify order '1' was still positioned relative to card '2' despite dropping on column
    const finalOrders = result.current.localOrders
    const order1Index = finalOrders.findIndex((o) => o.id === '1')
    const order2Index = finalOrders.findIndex((o) => o.id === '2')
    expect(order1Index).toBeLessThan(order2Index)
    expect(onStatusChange).toHaveBeenCalledWith('1', RO_STATUS.IN_PROGRESS)
  })

  it('should handle same-column drop on itself as no-op', () => {
    const onStatusChange = vi.fn()
    const clearSelection = vi.fn()
    const orders = [
      {
        id: '1',
        status: RO_STATUS.NEW,
        customer: { name: 'John Doe', phone: '555-0100' },
        vehicle: { year: 2020, make: 'Toyota', model: 'Camry' },
        services: [],
        approvedByCustomer: true,
        assignedTech: { id: 'tech-1', name: 'Tech 1', initials: 'T1', specialties: [], active: true },
        priority: 'NORMAL' as const,
        notes: '',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      },
      {
        id: '2',
        status: RO_STATUS.NEW,
        customer: { name: 'Jane Smith', phone: '555-0200' },
        vehicle: { year: 2021, make: 'Honda', model: 'Civic' },
        services: [],
        approvedByCustomer: true,
        assignedTech: null,
        priority: 'NORMAL' as const,
        notes: '',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      },
    ]

    const { result } = renderHook(() =>
      useKanbanDndHandlers({ orders, onStatusChange, clearSelection }),
    )

    // Drag over card '1' itself
    const dragOverEvent: DragOverEvent = {
      active: {
        id: '1',
        data: { current: {} },
        rect: {
          current: {
            initial: { top: 0, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
            translated: { top: 0, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
          },
        },
      },
      over: {
        id: '1',
        data: { current: {} },
        rect: { top: 0, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
        disabled: false,
      },
      delta: { x: 0, y: 0 },
      collisions: null,
      activatorEvent: null as any,
    }

    act(() => {
      result.current.handleDragOver(dragOverEvent)
    })

    // Drop on itself
    const dragEndEvent: DragEndEvent = {
      active: {
        id: '1',
        data: { current: {} },
        rect: {
          current: {
            initial: { top: 0, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
            translated: { top: 0, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
          },
        },
      },
      over: {
        id: '1',
        data: { current: {} },
        rect: { top: 0, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
        disabled: false,
      },
      delta: { x: 0, y: 0 },
      activatorEvent: null as any,
      collisions: null,
    }

    act(() => {
      result.current.handleDragEnd(dragEndEvent)
    })

    // Verify order didn't change and no status change was called
    const finalOrders = result.current.localOrders
    expect(finalOrders[0].id).toBe('1')
    expect(finalOrders[1].id).toBe('2')
    expect(onStatusChange).not.toHaveBeenCalled()
  })

  it('should insert at top of column when dropping over column top', () => {
    const onStatusChange = vi.fn()
    const clearSelection = vi.fn()
    const orders = [
      {
        id: '1',
        status: RO_STATUS.NEW,
        customer: { name: 'John Doe', phone: '555-0100' },
        vehicle: { year: 2020, make: 'Toyota', model: 'Camry' },
        services: [],
        approvedByCustomer: true,
        assignedTech: { id: 'tech-1', name: 'Tech 1', initials: 'T1', specialties: [], active: true },
        priority: 'NORMAL' as const,
        notes: '',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      },
      {
        id: '2',
        status: RO_STATUS.IN_PROGRESS,
        customer: { name: 'Jane Smith', phone: '555-0200' },
        vehicle: { year: 2021, make: 'Honda', model: 'Civic' },
        services: [],
        approvedByCustomer: true,
        assignedTech: { id: 'tech-1', name: 'Tech 1', initials: 'T1', specialties: [], active: true },
        priority: 'NORMAL' as const,
        notes: '',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      },
      {
        id: '3',
        status: RO_STATUS.IN_PROGRESS,
        customer: { name: 'Bob Johnson', phone: '555-0300' },
        vehicle: { year: 2019, make: 'Ford', model: 'F-150' },
        services: [],
        approvedByCustomer: true,
        assignedTech: { id: 'tech-1', name: 'Tech 1', initials: 'T1', specialties: [], active: true },
        priority: 'NORMAL' as const,
        notes: '',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z',
      },
    ]

    const { result } = renderHook(() =>
      useKanbanDndHandlers({ orders, onStatusChange, clearSelection }),
    )

    // Simulate drag over column top (activeY <= colY)
    const dragOverEvent: DragOverEvent = {
      active: {
        id: '1',
        data: { current: {} },
        rect: {
          current: {
            initial: { top: 0, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
            translated: { top: 50, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
          },
        },
      },
      over: {
        id: RO_STATUS.IN_PROGRESS,
        data: { current: {} },
        rect: { top: 0, left: 0, right: 0, bottom: 0, width: 300, height: 600 },
        disabled: false,
      },
      delta: { x: 0, y: 50 },
      collisions: null,
      activatorEvent: null as any,
    }

    act(() => {
      result.current.handleDragOver(dragOverEvent)
    })

    // Drop on column
    const dragEndEvent: DragEndEvent = {
      active: {
        id: '1',
        data: { current: {} },
        rect: {
          current: {
            initial: { top: 0, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
            translated: { top: 50, left: 0, right: 0, bottom: 0, width: 0, height: 50 },
          },
        },
      },
      over: {
        id: RO_STATUS.IN_PROGRESS,
        data: { current: {} },
        rect: { top: 0, left: 0, right: 0, bottom: 0, width: 300, height: 600 },
        disabled: false,
      },
      delta: { x: 0, y: 50 },
      activatorEvent: null as any,
      collisions: null,
    }

    act(() => {
      result.current.handleDragEnd(dragEndEvent)
    })

    // Verify order '1' was inserted before '2' (top of IN_PROGRESS column)
    const finalOrders = result.current.localOrders
    const order1Index = finalOrders.findIndex((o) => o.id === '1')
    const order2Index = finalOrders.findIndex((o) => o.id === '2')
    expect(order1Index).toBeLessThan(order2Index)
    expect(finalOrders[order1Index].status).toBe(RO_STATUS.IN_PROGRESS)
    expect(onStatusChange).toHaveBeenCalledWith('1', RO_STATUS.IN_PROGRESS)
  })
})
