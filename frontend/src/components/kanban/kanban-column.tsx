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
}

export function KanbanColumn({ status, orders, title, color }: KanbanColumnProps) {
  const { setNodeRef, isOver } = useDroppable({ id: status })

  return (
    <div
      className={`flex min-w-[340px] flex-col gap-1 overflow-hidden rounded-lg transition-all ${
        isOver ? 'shadow-sm ring-2 ring-green-400' : ''
      }`}
    >
      {/* Discrete Colored Header */}
      <div className={`flex items-center gap-2 rounded-lg px-4 py-2 ${color} shadow-sm`}>
        <h3 className='text-sm font-bold uppercase'>{title}</h3>
        <span className='text-sm font-bold'>{orders.length}</span>
      </div>

      <SortableContext
        id={status}
        items={orders.map((o) => o.id)}
        strategy={verticalListSortingStrategy}
      >
        <ScrollArea className='h-[calc(100vh-280px)]'>
          <div ref={setNodeRef} className='flex min-h-[200px] flex-col gap-2 p-2 pt-3'>
            {orders.length === 0 ? (
              <div className='flex flex-1 items-center justify-center text-center'>
                <p className='text-sm text-gray-400'>No orders</p>
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
