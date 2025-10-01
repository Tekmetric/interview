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
  const { setNodeRef } = useDroppable({ id: status })

  return (
    <div className='flex min-w-[300px] flex-col gap-3 rounded-lg bg-gray-50 p-4'>
      <div className='flex items-center justify-between'>
        <h3 className='font-semibold text-gray-900'>{title}</h3>
        <div
          className={`flex h-6 w-6 items-center justify-center rounded-full text-xs font-semibold ${color}`}
        >
          {orders.length}
        </div>
      </div>

      <SortableContext
        id={status}
        items={orders.map((o) => o.id)}
        strategy={verticalListSortingStrategy}
      >
        <div ref={setNodeRef} className='flex min-h-[200px] flex-col gap-2'>
          {orders.map((order) => (
            <KanbanCard key={order.id} order={order} />
          ))}
        </div>
      </SortableContext>
    </div>
  )
}
