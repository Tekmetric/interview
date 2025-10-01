import { DndContext, DragEndEvent, DragOverlay, DragStartEvent } from '@dnd-kit/core'
import { useState } from 'react'
import { KanbanColumn } from './kanban-column'
import { KanbanCard } from './kanban-card'
import type { RepairOrder, RepairOrderStatus } from '@shared/types'

type KanbanBoardProps = {
  orders: RepairOrder[]
  onStatusChange: (orderId: string, newStatus: RepairOrderStatus) => void
}

const COLUMNS: Array<{
  status: RepairOrderStatus
  title: string
  color: string
}> = [
  { status: 'NEW', title: 'New', color: 'bg-blue-500 text-white' },
  {
    status: 'AWAITING_APPROVAL',
    title: 'Awaiting Approval',
    color: 'bg-amber-500 text-white',
  },
  { status: 'IN_PROGRESS', title: 'In Progress', color: 'bg-indigo-500 text-white' },
  { status: 'WAITING_PARTS', title: 'Waiting Parts', color: 'bg-orange-500 text-white' },
  { status: 'COMPLETED', title: 'Completed', color: 'bg-green-500 text-white' },
]

export function KanbanBoard({ orders, onStatusChange }: KanbanBoardProps) {
  const [activeId, setActiveId] = useState<string | null>(null)

  const activeOrder = activeId ? orders.find((o) => o.id === activeId) : null

  const handleDragStart = (event: DragStartEvent) => {
    setActiveId(event.active.id as string)
  }

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event

    if (!over) {
      setActiveId(null)
      return
    }

    const orderId = active.id as string
    const newStatus = over.id as RepairOrderStatus

    const order = orders.find((o) => o.id === orderId)
    if (order && order.status !== newStatus) {
      onStatusChange(orderId, newStatus)
    }

    setActiveId(null)
  }

  const groupedOrders = COLUMNS.reduce(
    (acc, col) => {
      acc[col.status] = orders.filter((o) => o.status === col.status)
      return acc
    },
    {} as Record<RepairOrderStatus, RepairOrder[]>,
  )

  return (
    <DndContext onDragStart={handleDragStart} onDragEnd={handleDragEnd}>
      <div className='flex gap-4 overflow-x-auto pb-4'>
        {COLUMNS.map((col) => (
          <KanbanColumn
            key={col.status}
            status={col.status}
            orders={groupedOrders[col.status]}
            title={col.title}
            color={col.color}
          />
        ))}
      </div>

      <DragOverlay>{activeOrder && <KanbanCard order={activeOrder} />}</DragOverlay>
    </DndContext>
  )
}
