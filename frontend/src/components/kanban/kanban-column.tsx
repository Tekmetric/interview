import { useDroppable } from '@dnd-kit/core'
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable'
import { ScrollArea } from '@/components/ui/scroll-area'
import { KanbanCard } from './kanban-card'
import type { RepairOrder, RepairOrderStatus } from '@shared/types'

type KanbanColumnProps = {
  status: RepairOrderStatus
  orders: RepairOrder[]
  title: string
  color: string
  isBeingDraggedOver: boolean
  isValidDropZone: boolean
  validationMessage?: string
}

export function KanbanColumn({
  status,
  orders,
  title,
  color,
  isBeingDraggedOver,
  isValidDropZone,
  validationMessage,
}: KanbanColumnProps) {
  const { setNodeRef } = useDroppable({ id: status })

  // Extract base color from Tailwind class (e.g., "bg-blue-100" -> "blue")
  const baseColor = color.match(/bg-(\w+)-/)?.[1] || 'gray'

  return (
    <div
      className={`relative flex min-w-[340px] flex-col gap-1.5 overflow-hidden transition-all ${
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
        <div className='absolute inset-0 z-10 flex items-center justify-center rounded-lg bg-red-50/95 p-4'>
          <div className='flex max-w-[280px] flex-col items-center gap-2 text-center'>
            <svg
              xmlns='http://www.w3.org/2000/svg'
              viewBox='0 0 24 24'
              fill='currentColor'
              className='h-8 w-8 text-red-500'
            >
              <path
                fillRule='evenodd'
                d='M9.401 3.003c1.155-2 4.043-2 5.197 0l7.355 12.748c1.154 2-.29 4.5-2.599 4.5H4.645c-2.309 0-3.752-2.5-2.598-4.5L9.4 3.003zM12 8.25a.75.75 0 01.75.75v3.75a.75.75 0 01-1.5 0V9a.75.75 0 01.75-.75zm0 8.25a.75.75 0 100-1.5.75.75 0 000 1.5z'
                clipRule='evenodd'
              />
            </svg>
            <p className='text-sm font-semibold text-red-700'>Cannot move order here</p>
            <p className='text-xs text-red-600'>{validationMessage}</p>
          </div>
        </div>
      )}

      <SortableContext
        id={status}
        items={orders.map((o) => o.id)}
        strategy={verticalListSortingStrategy}
      >
        <ScrollArea className='h-[calc(100vh-280px)]'>
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
                <KanbanCard key={order.id} order={order} showStatus={false} />
              ))
            )}
          </div>
        </ScrollArea>
      </SortableContext>
    </div>
  )
}
