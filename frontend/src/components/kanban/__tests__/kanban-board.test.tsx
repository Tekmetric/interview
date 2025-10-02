/* eslint-disable no-undef */
import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import { KanbanBoard, handleDragEnd } from '../kanban-board'
import { DragEndEvent } from '@dnd-kit/core'
import { RO_STATUS } from '@shared/constants'

describe('KanbanBoard', () => {
  const mockOrders = [
    { id: '1', status: RO_STATUS.NEW, customer: { name: 'John Doe' }, vehicle: { year: 2020, make: 'Toyota', model: 'Camry' }, services: [], approvedByCustomer: true, assignedTech: { id: 'tech-1', name: 'Tech 1'} },
    { id: '2', status: RO_STATUS.IN_PROGRESS, customer: { name: 'Jane Smith' }, vehicle: { year: 2021, make: 'Honda', model: 'Civic' }, services: [] },
  ]

  it('should render the correct number of columns with the correct titles', () => {
    render(<KanbanBoard orders={mockOrders} onStatusChange={vi.fn()} />)

    expect(screen.getByText('New')).toBeInTheDocument()
    expect(screen.getByText('Awaiting Approval')).toBeInTheDocument()
    expect(screen.getByText('In Progress')).toBeInTheDocument()
    expect(screen.getByText('Waiting Parts')).toBeInTheDocument()
    expect(screen.getByText('Completed')).toBeInTheDocument()
  })

  it('should call onStatusChange when a card is dropped into a new column', () => {
    const onStatusChange = vi.fn()
    const localOrders = [...mockOrders];

    const dragEndEvent: DragEndEvent = {
        active: { id: '1', data: { current: { sortable: { index: 0, items: ['1'] } } } },
        over: { id: RO_STATUS.IN_PROGRESS, data: { current: { sortable: { index: 0, items: [] } } } },
        delta: { x: 0, y: 0 },
      };

    // This is not ideal, but it's the only way to test the logic of the handleDragEnd function
    // without refactoring the component to be more testable.
    const setLocalOrders = vi.fn();
    const setActiveId = vi.fn();
    const setDragOverStatus = vi.fn();
    const setIsValidDrop = vi.fn();
    const setValidationMessage = vi.fn();
    const setDropIndicatorById = vi.fn();

    const handleDragEndWithMocks = handleDragEnd.bind({ 
        localOrders, 
        onStatusChange, 
        setLocalOrders, 
        setActiveId, 
        setDragOverStatus, 
        setIsValidDrop, 
        setValidationMessage, 
        setDropIndicatorById 
    });

    handleDragEndWithMocks(dragEndEvent);

    expect(onStatusChange).toHaveBeenCalledWith('1', RO_STATUS.IN_PROGRESS)
  })
})
