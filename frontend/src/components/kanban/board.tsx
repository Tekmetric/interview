'use client'

import { closestCenter, DndContext } from '@dnd-kit/core'
import { useMemo, useState } from 'react'

import { KANBAN_COLUMNS } from '@shared/constants'

import { useKanban } from './hooks/use-kanban'

import {
  KanbanColumn,
  KanbanCard,
  KanbanCardsList,
  KanbanContainer,
  KanbanHeader,
  KanbanOverlay,
  type DragEndEvent,
} from '@/components/kanban/kanban-atoms'

import { KanbanCardContent } from './card-content'
import type { RepairOrder, RepairOrderStatus } from '@shared/types'

// Adapter type that adds column and name fields for drag-and-drop library
type KanbanRepairOrder = RepairOrder & { column: RepairOrderStatus; name: string }

type KanbanBoardProps = {
  orders: RepairOrder[]
  onStatusChange?: (orderId: string, newStatus: RepairOrderStatus) => void
}

type KanbanColumn = (typeof KANBAN_COLUMNS)[number]

export const KanbanBoard = ({ orders, onStatusChange }: KanbanBoardProps) => {
  // Initialize board data with column field mapped from status
  const [boardData, setBoardData] = useState<KanbanRepairOrder[]>(() =>
    orders.map((order) => ({ ...order, column: order.status, name: order.id })),
  )

  // Update board data when orders prop changes
  useMemo(() => {
    setBoardData(
      orders.map((order) => ({ ...order, column: order.status, name: order.id })),
    )
  }, [orders])

  // Use KANBAN_COLUMNS directly - they already have the correct shape
  const columns = KANBAN_COLUMNS

  const handleDataChange = (newData: KanbanRepairOrder[]) => {
    setBoardData(newData)
  }

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event

    if (!over || !onStatusChange) return

    const draggedOrder = boardData.find((o) => o.id === active.id)
    if (!draggedOrder) return

    // Find the new status from the column
    const overColumn = columns.find(
      (col) =>
        col.status === over.id ||
        boardData.some((o) => o.id === over.id && o.column === col.status),
    )

    if (overColumn && draggedOrder.status !== overColumn.status) {
      onStatusChange(draggedOrder.id, overColumn.status)
    }
  }

  const {
    sensors,
    activeCard,
    handleDragStart,
    handleDragOver,
    handleDragEnd: onDragEnd,
  } = useKanban<KanbanRepairOrder, KanbanColumn>({
    columns: [...columns],
    data: boardData,
    columnField: 'column',
    onDataChange: handleDataChange,
    onDragEnd: handleDragEnd,
  })

  return (
    <DndContext
      collisionDetection={closestCenter}
      onDragEnd={onDragEnd}
      onDragOver={handleDragOver}
      onDragStart={handleDragStart}
      sensors={sensors}
    >
      <KanbanContainer>
        {columns.map((column) => {
          const columnOrders = boardData.filter((o) => o.column === column.status)

          return (
            <KanbanColumn column={column} key={column.status}>
              <KanbanHeader>
                <div
                  className={`flex items-center justify-between px-3 py-2.5 ${column.color} rounded-lg border-b border-gray-200/50`}
                >
                  <h3 className='text-xs font-semibold tracking-wide uppercase'>
                    {column.title}
                  </h3>
                  <span
                    className='rounded-full bg-white/40 px-2 py-0.5 text-xs font-semibold'
                    aria-label={`${columnOrders.length} orders`}
                  >
                    {columnOrders.length}
                  </span>
                </div>
              </KanbanHeader>
              <KanbanCardsList data={boardData} id={column.status} columnField='column'>
                {(order: KanbanRepairOrder) => (
                  <KanbanCard
                    column={order.column}
                    id={order.id}
                    key={order.id}
                    name={order.id}
                  >
                    {({ isDragging }) => (
                      <KanbanCardContent order={order} isDragging={isDragging} />
                    )}
                  </KanbanCard>
                )}
              </KanbanCardsList>
            </KanbanColumn>
          )
        })}
      </KanbanContainer>

      <KanbanOverlay>
        {activeCard && <KanbanCardContent order={activeCard} isDragOverlay />}
      </KanbanOverlay>
    </DndContext>
  )
}
