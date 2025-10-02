import { useDroppable } from '@dnd-kit/core'
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable'
import { ScrollArea } from '@/components/ui/scroll-area'
import { KanbanCard } from './kanban-card'
import { InvalidDropOverlay } from './invalid-drop-overlay'
import type { RepairOrder, RepairOrderStatus } from '@shared/types'

type KanbanColumnProps = {
  status: RepairOrderStatus
  orders: RepairOrder[]
  title: string
  color: string
  isBeingDraggedOver: boolean
  isValidDropZone: boolean
  validationMessage?: string
  dropIndicatorById?: Record<string, 'top' | 'bottom'>
}

export function KanbanColumn({
  status,
  orders,
  title,
  color,
  isBeingDraggedOver,
  isValidDropZone,
  validationMessage,
  dropIndicatorById = {},
}: KanbanColumnProps) {
  const { setNodeRef } = useDroppable({ id: status })

  // Extract base color from Tailwind class (e.g., "bg-blue-100" -> "blue")
  const baseColor = color.match(/bg-(\w+)-/)?.[1] || 'gray'

  return (
    <div
      className={`relative flex min-w-[340px] flex-col gap-1.5 overflow-hidden rounded-lg transition-all ${
        isBeingDraggedOver && !isValidDropZone ? 'animate-pulse ring-2 ring-red-400' : ''
      }`}
    >
      {/* Discrete Colored Header */}
      <div
        className={`flex items-center justify-between px-3 py-2.5 ${color} rounded-lg border-b border-gray-200/50`}
      >
        <h3 className='text-xs font-semibold tracking-wide uppercase'>{title}</h3>
        <span className='rounded-full bg-white/40 px-2 py-0.5 text-xs font-semibold'>
          {orders.length}
        </span>
      </div>

      {/* Invalid Drop Overlay Message */}
      {isBeingDraggedOver && !isValidDropZone && validationMessage && (
        <InvalidDropOverlay validationMessage={validationMessage} />
      )}

      <SortableContext
        id={status}
        items={orders.map((o) => o.id)}
        strategy={verticalListSortingStrategy}
      >
        <ScrollArea className='h-[calc(100vh-220px)]'>
          <div
            ref={setNodeRef}
            className={`flex min-h-[200px] flex-col gap-2 p-2 transition-colors ${
              isBeingDraggedOver ? `bg-${baseColor}-50/50` : ''
            }`}
          >
            {orders.length === 0 ? (
              <div className='flex flex-1 items-center justify-center text-center'>
                <p className='text-xs text-gray-400'>No orders</p>
              </div>
            ) : (
              orders.map((order) => (
                <KanbanCard
                  key={order.id}
                  order={order}
                  showStatus={false}
                  dropPosition={dropIndicatorById[order.id]}
                />
              ))
            )}
          </div>
        </ScrollArea>
      </SortableContext>
    </div>
  )
}
