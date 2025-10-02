import { useMemo } from 'react'
import {
  DndContext,
  DragOverlay,
  MouseSensor,
  TouchSensor,
  KeyboardSensor,
  useSensor,
  useSensors,
  closestCorners,
} from '@dnd-kit/core'
import { ScrollArea, ScrollBar } from '@/components/ui/scroll-area'
import { KANBAN_COLUMNS } from '@shared/constants'
import type { RepairOrder, RepairOrderStatus } from '@shared/types'
import { useMultiSelect } from '@/hooks/use-multi-select'

import { KanbanColumn } from './kanban-column'
import { KanbanCard } from './kanban-card'
import { useKanbanDndHandlers } from './hooks/use-kanban-dnd-handlers'

type KanbanBoardProps = {
  orders: RepairOrder[]
  onStatusChange: (orderId: string, newStatus: RepairOrderStatus) => void
}

export function KanbanBoard({ orders, onStatusChange }: KanbanBoardProps) {
  const { clearSelection } = useMultiSelect()

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

  const {
    dragOverStatus,
    isValidDrop,
    validationMessage,
    localOrders,
    dropIndicatorById,
    activeOrder,
    handleDragStart,
    handleDragOver,
    handleDragEnd,
  } = useKanbanDndHandlers({ orders, onStatusChange, clearSelection })

  const groupedOrders = useMemo(() => {
    return KANBAN_COLUMNS.reduce(
      (acc, col) => {
        acc[col.status] = localOrders.filter((o) => o.status === col.status)
        return acc
      },
      {} as Record<RepairOrderStatus, RepairOrder[]>,
    )
  }, [localOrders])

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCorners}
      onDragStart={handleDragStart}
      onDragOver={handleDragOver}
      onDragEnd={handleDragEnd}
    >
      <ScrollArea className='w-full'>
        <div className='flex gap-3 bg-gray-50/50 p-3'>
          {KANBAN_COLUMNS.map((col) => (
            <KanbanColumn
              key={col.status}
              status={col.status}
              orders={groupedOrders[col.status]}
              title={col.title}
              color={col.color}
              isBeingDraggedOver={dragOverStatus === col.status}
              isValidDropZone={isValidDrop}
              validationMessage={validationMessage}
              dropIndicatorById={dropIndicatorById}
            />
          ))}
        </div>
        <ScrollBar orientation='horizontal' />
      </ScrollArea>

      <DragOverlay>
        {activeOrder && (
          <div className='scale-105 rotate-3 opacity-80'>
            <KanbanCard order={activeOrder} />
          </div>
        )}
      </DragOverlay>
    </DndContext>
  )
}
