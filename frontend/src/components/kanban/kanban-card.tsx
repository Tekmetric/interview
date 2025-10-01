import { useSortable } from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'
import { Badge } from '@/components/ui/badge'
import { useLocation, useSearch } from 'wouter'
import type { RepairOrder } from '@shared/types'

type KanbanCardProps = {
  order: RepairOrder
}

export function KanbanCard({ order }: KanbanCardProps) {
  const [, setLocation] = useLocation()
  const searchParams = new URLSearchParams(useSearch())
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } =
    useSortable({
      id: order.id,
    })

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  }

  const priorityColors = {
    HIGH: 'border-red-500 text-red-700',
    NORMAL: 'border-gray-300 text-gray-600',
  }

  const handleClick = (e: React.MouseEvent) => {
    // Don't open drawer if dragging
    if (isDragging) return
    
    e.stopPropagation()
    searchParams.set('roId', order.id)
    setLocation(`?${searchParams.toString()}`)
  }

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      className='cursor-move rounded-lg border bg-white p-3 shadow-sm hover:shadow-md'
    >
      <div {...listeners} className='flex flex-col gap-2' onClick={handleClick}>
      <div className='flex flex-col gap-2'>
        <div className='flex items-center justify-between'>
          <p className='font-mono text-sm font-semibold'>{order.id}</p>
          {order.priority === 'HIGH' && (
            <Badge variant='outline' className={priorityColors.HIGH}>
              HIGH
            </Badge>
          )}
        </div>

        <p className='text-sm font-medium text-gray-900'>
          {order.vehicle.year} {order.vehicle.make} {order.vehicle.model}
        </p>

        <p className='text-xs text-gray-500'>{order.customer.name}</p>

        {order.assignedTech && (
          <div className='flex items-center gap-1 text-xs text-gray-600'>
            <svg
              xmlns='http://www.w3.org/2000/svg'
              viewBox='0 0 24 24'
              fill='none'
              stroke='currentColor'
              strokeWidth='2'
              className='h-3 w-3'
            >
              <path d='M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2' />
              <circle cx='12' cy='7' r='4' />
            </svg>
            {order.assignedTech.name}
          </div>
        )}

        {order.dueTime && (
          <p className='text-xs text-gray-500'>
            Due: {new Date(order.dueTime).toLocaleDateString()}
          </p>
        )}

        {order.services.length > 0 && (
          <div className='flex flex-wrap gap-1'>
            {order.services.slice(0, 2).map((service, idx) => (
              <Badge key={idx} variant='outline' className='text-xs'>
                {service}
              </Badge>
            ))}
            {order.services.length > 2 && (
              <Badge variant='outline' className='text-xs'>
                +{order.services.length - 2}
              </Badge>
            )}
          </div>
        )}
      </div>
      </div>
    </div>
  )
}
