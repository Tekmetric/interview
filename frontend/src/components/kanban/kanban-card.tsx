import { useSortable } from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'
import { Badge } from '@/components/ui/badge'
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip'
import { useLocation, useSearch } from 'wouter'
import type { RepairOrder } from '@shared/types'

type KanbanCardProps = {
  order: RepairOrder
  showStatus?: boolean
}

export function KanbanCard({ order, showStatus = true }: KanbanCardProps) {
  const [, setLocation] = useLocation()
  const searchParams = new URLSearchParams(useSearch())
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: order.id,
  })

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.3 : 1,
  }

  const handleClick = () => {
    if (isDragging) return
    searchParams.set('roId', order.id)
    setLocation(`?${searchParams.toString()}`)
  }

  const statusConfig = {
    NEW: { bg: 'bg-blue-50', text: 'text-blue-700', border: 'border-blue-200', label: 'NEW' },
    AWAITING_APPROVAL: {
      bg: 'bg-amber-50',
      text: 'text-amber-700',
      border: 'border-amber-200',
      label: 'AWAITING_APPROVAL',
    },
    IN_PROGRESS: {
      bg: 'bg-indigo-50',
      text: 'text-indigo-700',
      border: 'border-indigo-200',
      label: 'IN_PROGRESS',
    },
    WAITING_PARTS: {
      bg: 'bg-purple-50',
      text: 'text-purple-700',
      border: 'border-purple-200',
      label: 'WAITING_PARTS',
    },
    COMPLETED: {
      bg: 'bg-green-50',
      text: 'text-green-700',
      border: 'border-green-200',
      label: 'COMPLETED',
    },
  }

  const status = statusConfig[order.status]

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      className='group relative cursor-move rounded-lg border border-gray-200 bg-white p-4 transition-all hover:border-gray-300 hover:shadow-sm active:cursor-grabbing'
      onClick={handleClick}
    >
      {/* High Priority Fire Icon - Absolute positioned top-right */}
      {order.priority === 'HIGH' && (
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger asChild>
              <div className='absolute right-3 top-3'>
                <svg
                  xmlns='http://www.w3.org/2000/svg'
                  viewBox='0 0 24 24'
                  fill='currentColor'
                  className='h-5 w-5 text-red-500'
                >
                  <path
                    fillRule='evenodd'
                    d='M12.963 2.286a.75.75 0 00-1.071-.136 9.742 9.742 0 00-3.539 6.177A7.547 7.547 0 016.648 6.61a.75.75 0 00-1.152-.082A9 9 0 1015.68 4.534a7.46 7.46 0 01-2.717-2.248zM15.75 14.25a3.75 3.75 0 11-7.313-1.172c.628.465 1.35.81 2.133 1a5.99 5.99 0 011.925-3.545 3.75 3.75 0 013.255 3.717z'
                    clipRule='evenodd'
                  />
                </svg>
              </div>
            </TooltipTrigger>
            <TooltipContent>
              <p>High Priority</p>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      )}

      <div className='flex flex-col gap-2'>
        <div className='flex items-center gap-2'>
          <span className='text-base font-bold text-gray-900'>{order.id}</span>
          {showStatus && (
            <Badge
              variant='outline'
              className={`${status.bg} ${status.text} ${status.border} text-xs font-medium`}
            >
              {status.label}
            </Badge>
          )}
        </div>
        <p className='text-sm font-semibold text-gray-800'>
          {order.vehicle.year} {order.vehicle.make} {order.vehicle.model}
        </p>
        <div className='flex items-center gap-3 text-xs text-gray-500'>
          <span>{order.customer.name}</span>
          {order.dueTime && (
            <>
              <span>•</span>
              <span>Due: {new Date(order.dueTime).toLocaleString()}</span>
            </>
          )}
        </div>
      </div>
    </div>
  )
}
