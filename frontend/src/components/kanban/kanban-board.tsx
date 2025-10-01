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
  closestCorners,
} from '@dnd-kit/core'
import { useState } from 'react'
import { ScrollArea, ScrollBar } from '@/components/ui/scroll-area'
import { toast } from 'sonner'
import { canTransition } from '@shared/transitions'
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
  const [dragOverStatus, setDragOverStatus] = useState<RepairOrderStatus | null>(null)
  const [isValidDrop, setIsValidDrop] = useState<boolean>(true)
  const [validationMessage, setValidationMessage] = useState<string>('')

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

    if (!over) {
      setDragOverStatus(null)
      setIsValidDrop(true)
      setValidationMessage('')
      return
    }

    const activeId = active.id as string
    const overId = over.id as string

    // Get the order being dragged
    const order = orders.find((o) => o.id === activeId)
    if (!order) return

    // Check if we're over a column (status ID) or a card
    const overStatus = COLUMNS.find((col) => col.status === overId)?.status
    const overCard = orders.find((o) => o.id === overId)

    let targetStatus: RepairOrderStatus | null = null

    if (overStatus) {
      targetStatus = overStatus
    } else if (overCard) {
      targetStatus = overCard.status
    }

    if (targetStatus) {
      setDragOverStatus(targetStatus)

      // Validate the transition (visual feedback only, no status change)
      const validation = canTransition(order.status, targetStatus, order)
      setIsValidDrop(validation.allowed)
      setValidationMessage(validation.reason || '')
    }
  }

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event

    // Reset drag state
    setActiveId(null)
    setDragOverStatus(null)
    setIsValidDrop(true)
    setValidationMessage('')

    if (!over) {
      return
    }

    const orderId = active.id as string
    const overId = over.id as string

    // Get the order being dropped
    const order = orders.find((o) => o.id === orderId)
    if (!order) return

    // Check if we're dropping on a column or a card
    const overStatus = COLUMNS.find((col) => col.status === overId)?.status
    const overCard = orders.find((o) => o.id === overId)

    let targetStatus: RepairOrderStatus | null = null

    if (overStatus) {
      targetStatus = overStatus
    } else if (overCard) {
      targetStatus = overCard.status
    }

    // No status change needed
    if (!targetStatus || order.status === targetStatus) {
      return
    }

    // Validate the transition before updating
    const validation = canTransition(order.status, targetStatus, order)

    if (!validation.allowed) {
      // Show error toast with reason
      toast.error('Cannot move order', {
        description: validation.reason || 'This transition is not allowed',
        duration: 3000,
      })
      return
    }

    // Valid transition - update status
    onStatusChange(orderId, targetStatus)
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
      collisionDetection={closestCorners}
      onDragStart={handleDragStart}
      onDragOver={handleDragOver}
      onDragEnd={handleDragEnd}
    >
      <ScrollArea className='w-full'>
        <div className='flex gap-3 p-3 bg-gray-50/50'>
          {COLUMNS.map((col) => (
            <KanbanColumn
              key={col.status}
              status={col.status}
              orders={groupedOrders[col.status]}
              title={col.title}
              color={col.color}
              isBeingDraggedOver={dragOverStatus === col.status}
              isValidDropZone={isValidDrop}
              validationMessage={validationMessage}
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
