import {
  DndContext,
  DragEndEvent,
  DragOverlay,
  DragStartEvent,
  DragOverEvent,
  MouseSensor,
  TouchSensor,
  KeyboardSensor,
  useSensor,
  useSensors,
  closestCenter,
} from '@dnd-kit/core'
import { useState } from 'react'
import { ScrollArea, ScrollBar } from '@/components/ui/scroll-area'
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
  { status: 'NEW', title: 'New', color: 'bg-blue-100 text-blue-700' },
  {
    status: 'AWAITING_APPROVAL',
    title: 'Awaiting Approval',
    color: 'bg-amber-100 text-amber-700',
  },
  { status: 'IN_PROGRESS', title: 'In Progress', color: 'bg-indigo-100 text-indigo-700' },
  { status: 'WAITING_PARTS', title: 'Waiting Parts', color: 'bg-orange-100 text-orange-700' },
  { status: 'COMPLETED', title: 'Completed', color: 'bg-green-100 text-green-700' },
]

export function KanbanBoard({ orders, onStatusChange }: KanbanBoardProps) {
  const [activeId, setActiveId] = useState<string | null>(null)

  const sensors = useSensors(
    useSensor(MouseSensor, {
      activationConstraint: {
        distance: 8,
      },
    }),
    useSensor(TouchSensor, {
      activationConstraint: {
        delay: 200,
        tolerance: 6,
      },
    }),
    useSensor(KeyboardSensor),
  )

  const activeOrder = activeId ? orders.find((o) => o.id === activeId) : null

  const handleDragStart = (event: DragStartEvent) => {
    setActiveId(event.active.id as string)
  }

  const handleDragOver = (event: DragOverEvent) => {
    const { active, over } = event

    if (!over) return

    const activeId = active.id as string
    const overId = over.id as string

    // Check if we're over a column (status ID) or a card
    const overStatus = COLUMNS.find((col) => col.status === overId)?.status
    const overCard = orders.find((o) => o.id === overId)

    if (overStatus) {
      // Dragging over a column
      const order = orders.find((o) => o.id === activeId)
      if (order && order.status !== overStatus) {
        onStatusChange(activeId, overStatus)
      }
    } else if (overCard) {
      // Dragging over another card - adopt its status
      const order = orders.find((o) => o.id === activeId)
      if (order && order.status !== overCard.status) {
        onStatusChange(activeId, overCard.status)
      }
    }
  }

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event

    if (!over) {
      setActiveId(null)
      return
    }

    const orderId = active.id as string
    const overId = over.id as string

    // Check if we're dropping on a column
    const overStatus = COLUMNS.find((col) => col.status === overId)?.status
    const overCard = orders.find((o) => o.id === overId)

    if (overStatus) {
      const order = orders.find((o) => o.id === orderId)
      if (order && order.status !== overStatus) {
        onStatusChange(orderId, overStatus)
      }
    } else if (overCard) {
      const order = orders.find((o) => o.id === orderId)
      if (order && order.status !== overCard.status) {
        onStatusChange(orderId, overCard.status)
      }
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
    <DndContext
      sensors={sensors}
      collisionDetection={closestCenter}
      onDragStart={handleDragStart}
      onDragOver={handleDragOver}
      onDragEnd={handleDragEnd}
    >
      <ScrollArea className='w-full'>
        <div className='flex gap-4 p-4'>
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
        <ScrollBar orientation='horizontal' />
      </ScrollArea>

      <DragOverlay>
        {activeOrder && (
          <div className='rotate-3 scale-105 opacity-80'>
            <KanbanCard order={activeOrder} />
          </div>
        )}
      </DragOverlay>
    </DndContext>
  )
}
