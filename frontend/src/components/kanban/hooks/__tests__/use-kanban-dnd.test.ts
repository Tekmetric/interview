/* eslint-disable no-undef */
import { describe, it, expect, vi } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { useKanbanDnd } from '../use-kanban-dnd'
import { DragEndEvent } from '@dnd-kit/core'
import { RO_STATUS } from '@shared/constants'

describe('useKanbanDnd', () => {
  const mockOrders = [
    {
      id: '1',
      status: RO_STATUS.NEW,
      customer: { name: 'John Doe' },
      vehicle: { year: 2020, make: 'Toyota', model: 'Camry' },
      services: [],
      approvedByCustomer: true,
      assignedTech: { id: 'tech-1', name: 'Tech 1' },
    },
    {
      id: '2',
      status: RO_STATUS.IN_PROGRESS,
      customer: { name: 'Jane Smith' },
      vehicle: { year: 2021, make: 'Honda', model: 'Civic' },
      services: [],
    },
  ]

  it('should call onStatusChange when a card is dropped into a new column', () => {
    const onStatusChange = vi.fn()
    const { result } = renderHook(() => useKanbanDnd(mockOrders, onStatusChange))

    const dragEndEvent: DragEndEvent = {
      active: { id: '1', data: { current: { sortable: { index: 0, items: ['1'] } } } },
      over: {
        id: RO_STATUS.IN_PROGRESS,
        data: { current: { sortable: { index: 0, items: [] } } },
      },
      delta: { x: 0, y: 0 },
    }

    act(() => {
      result.current.handleDragEnd(dragEndEvent)
    })

    expect(onStatusChange).toHaveBeenCalledWith('1', RO_STATUS.IN_PROGRESS)
  })

  it('should not call onStatusChange when a card is dropped into the same column', () => {
    const onStatusChange = vi.fn()
    const { result } = renderHook(() => useKanbanDnd(mockOrders, onStatusChange))

    const dragEndEvent: DragEndEvent = {
      active: { id: '1', data: { current: { sortable: { index: 0, items: ['1'] } } } },
      over: {
        id: RO_STATUS.NEW,
        data: { current: { sortable: { index: 0, items: [] } } },
      },
      delta: { x: 0, y: 0 },
    }

    act(() => {
      result.current.handleDragEnd(dragEndEvent)
    })

    expect(onStatusChange).not.toHaveBeenCalled()
  })

  it('should not call onStatusChange when the transition is not allowed', () => {
    const onStatusChange = vi.fn()
    const { result } = renderHook(() => useKanbanDnd(mockOrders, onStatusChange))

    const dragEndEvent: DragEndEvent = {
      active: { id: '1', data: { current: { sortable: { index: 0, items: ['1'] } } } },
      over: {
        id: RO_STATUS.COMPLETED,
        data: { current: { sortable: { index: 0, items: [] } } },
      },
      delta: { x: 0, y: 0 },
    }

    act(() => {
      result.current.handleDragEnd(dragEndEvent)
    })

    expect(onStatusChange).not.toHaveBeenCalled()
  })
})
