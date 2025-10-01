import { useDroppable } from '@dnd-kit/core'
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable'
import { KanbanCard } from './kanban-card'
import type { RepairOrder, RepairOrderStatus } from '@shared/types'

type KanbanColumnProps = {
  status: RepairOrderStatus
  orders: RepairOrder[]
  title: string
  color: string
}

export function KanbanColumn({ status, orders, title, color }: KanbanColumnProps) {
  const { setNodeRef, isOver } = useDroppable({ id: status })

  return (
    <div
      className={`flex min-w-[340px] flex-col gap-3 rounded-xl bg-white p-4 shadow-sm transition-all ${
        isOver ? 'ring-2 ring-green-400 shadow-lg' : ''
      }`}
    >
      <div className='flex items-center justify-between'>
        <h3 className='text-sm font-semibold text-gray-700'>{title}</h3>
        <div
          className={`flex h-7 min-w-[28px] items-center justify-center rounded-full px-2 text-xs font-bold ${color}`}
        >
          {orders.length}
        </div>
      </div>

      <SortableContext
        id={status}
        items={orders.map((o) => o.id)}
        strategy={verticalListSortingStrategy}
      >
        <div
          ref={setNodeRef}
          className='flex max-h-[calc(100vh-280px)] min-h-[200px] flex-col gap-2 overflow-y-auto rounded-lg bg-gray-50/50 p-2 scrollbar-thin scrollbar-track-transparent scrollbar-thumb-gray-300'
        >
          {orders.length === 0 ? (
            <div className='flex flex-1 items-center justify-center text-center'>
              <p className='text-sm text-gray-400'>No orders</p>
            </div>
          ) : (
            orders.map((order) => <KanbanCard key={order.id} order={order} />)
          )}
        </div>
      </SortableContext>
    </div>
  )
}
