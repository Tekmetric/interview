import { Badge } from '@/components/ui/badge'
import type { RepairOrder } from '@shared/types'

type RepairOrderItemProps = {
  order: RepairOrder
}

export function RepairOrderItem({ order }: RepairOrderItemProps) {
  const statusColors = {
    NEW: 'bg-blue-500',
    AWAITING_APPROVAL: 'bg-amber-500',
    IN_PROGRESS: 'bg-indigo-500',
    WAITING_PARTS: 'bg-orange-500',
    COMPLETED: 'bg-green-500',
  }

  const priorityColors = {
    HIGH: 'border-red-500 text-red-700',
    NORMAL: 'border-gray-300 text-gray-600',
  }

  return (
    <div className='flex items-center justify-between rounded-lg border bg-white p-3 hover:bg-gray-50'>
      <div className='flex flex-col gap-1'>
        <div className='flex items-center gap-2'>
          <p className='font-mono text-sm font-semibold'>{order.id}</p>
          {order.priority === 'HIGH' && (
            <Badge variant='outline' className={priorityColors.HIGH}>
              HIGH
            </Badge>
          )}
        </div>
        <p className='text-sm text-gray-900'>
          {order.vehicle.year} {order.vehicle.make} {order.vehicle.model}
        </p>
        <p className='text-xs text-gray-500'>{order.customer.name}</p>
        {order.dueTime && (
          <p className='text-xs text-gray-500'>
            Due: {new Date(order.dueTime).toLocaleString()}
          </p>
        )}
      </div>
      <Badge className={statusColors[order.status]}>{order.status}</Badge>
    </div>
  )
}
