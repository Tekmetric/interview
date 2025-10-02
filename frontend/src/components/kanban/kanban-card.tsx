import { type MouseEvent } from 'react'
import { useSortable } from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip'
import { Checkbox } from '@/components/ui/checkbox'
import { useLocation, useSearch } from 'wouter'
import type { RepairOrder } from '@shared/types'
import { STATUS_CONFIG } from '@/components/repair-order/ro-constants'
import { REPAIR_ORDER_LABELS, FILTER_LABELS } from '@shared/constants'
import { usePreferences } from '@/hooks/use-preferences'

import { useMultiSelect } from '@/hooks/use-multi-select'
import { Eye } from 'lucide-react'

type KanbanCardProps = {
  order: RepairOrder
  showStatus?: boolean
  dropPosition?: 'top' | 'bottom'
}

export function KanbanCard({ order, showStatus = true, dropPosition }: KanbanCardProps) {
  const [, setLocation] = useLocation()
  const searchParams = new URLSearchParams(useSearch())
  const { preferences } = usePreferences()
  const { isSelected, toggleSelection, selection } = useMultiSelect()
  const { attributes, listeners, setNodeRef, transform, transition, isDragging, isOver } =
    useSortable({
      id: order.id,
    })

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 1 : 1,
  }

  const handleClick = () => {
    if (isDragging) return

    // When in selection mode, clicking card toggles selection
    if (selection.isSelecting) {
      toggleSelection(order.id)
      return
    }

    // Otherwise, open details drawer (existing behavior)
    searchParams.set('roId', order.id)
    setLocation(`?${searchParams.toString()}`)
  }

  const handleViewDetails = (e: MouseEvent) => {
    e.stopPropagation()
    searchParams.set('roId', order.id)
    setLocation(`?${searchParams.toString()}`)
  }

  const status = STATUS_CONFIG[order.status]

  return (
    <div className='relative'>
      {isDragging && (
        <div className='absolute inset-0 z-0 rounded-lg border-2 border-dashed border-blue-400 bg-blue-50/50' />
      )}
      {isOver && (
        <>
          {dropPosition !== 'bottom' && (
            <div className='absolute -top-1 right-0 left-0 h-0.5 rounded-full bg-blue-500' />
          )}
          {dropPosition === 'bottom' && (
            <div className='absolute right-0 -bottom-1 left-0 h-0.5 rounded-full bg-blue-500' />
          )}
        </>
      )}

      <div
        ref={setNodeRef}
        style={style}
        {...attributes}
        {...listeners}
        className={`group relative z-10 rounded-lg border p-4 transition-all hover:shadow-sm ${
          isSelected(order.id)
            ? 'border-blue-500 bg-blue-50'
            : 'border-gray-200 bg-white hover:border-gray-300'
        } ${selection.isSelecting ? 'cursor-pointer' : 'cursor-move active:cursor-grabbing'}`}
        onClick={handleClick}
      >
        <div className='absolute top-3 right-3 z-20 flex items-center gap-2'>
          <Button
            onClick={handleViewDetails}
            variant='outline'
            size='icon'
            className={`h-8 w-8 rounded-full transition-opacity ${selection.isSelecting ? 'opacity-0' : 'opacity-0 group-hover:opacity-100'}`}
            aria-label='View details'
          >
            <Eye />
          </Button>

          <Checkbox
            checked={isSelected(order.id)}
            onCheckedChange={() => toggleSelection(order.id)}
            onClick={(e) => e.stopPropagation()}
            className={`h-5 w-5 transition-opacity ${selection.isSelecting ? 'opacity-100' : 'opacity-0 group-hover:opacity-100'}`}
          />
        </div>

        <div className='flex flex-col gap-2'>
          <div className='flex items-center gap-2'>
            <span className='text-base font-bold text-gray-900'>{order.id}</span>

            {order.priority === 'HIGH' && (
              <TooltipProvider>
                <Tooltip>
                  <TooltipTrigger asChild>
                    <div>
                      <svg
                        xmlns='http://www.w3.org/2000/svg'
                        viewBox='0 0 24 24'
                        fill='currentColor'
                        className='h-4 w-4 text-red-500'
                        aria-label={FILTER_LABELS.HIGH_PRIORITY}
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
                    <p>{FILTER_LABELS.HIGH_PRIORITY}</p>
                  </TooltipContent>
                </Tooltip>
              </TooltipProvider>
            )}

            {showStatus && (
              <Badge
                variant='outline'
                className={`${status.bg} ${status.text} ${status.border} text-xs font-medium`}
              >
                {status.label}
              </Badge>
            )}
          </div>

          {preferences.columnVisibility.vehicleDetails && (
            <p className='text-sm font-semibold text-gray-800'>
              {order.vehicle.year} {order.vehicle.make} {order.vehicle.model}
            </p>
          )}

          <div className='flex items-center gap-3 text-xs text-gray-500'>
            <span>{order.customer.name}</span>
            {preferences.columnVisibility.customerPhone && order.customer.phone && (
              <>
                <span>•</span>
                <span>{order.customer.phone}</span>
              </>
            )}
            {preferences.columnVisibility.dueTime && order.dueTime && (
              <>
                <span>•</span>
                <span>
                  {REPAIR_ORDER_LABELS.DUE} {new Date(order.dueTime).toLocaleString()}
                </span>
              </>
            )}
          </div>
        </div>

        <div className='mt-2 flex flex-col gap-2'>
          {preferences.columnVisibility.services && order.services.length > 0 && (
            <div className='flex flex-wrap gap-1'>
              {order.services.slice(0, 2).map((service) => (
                <Badge key={service} variant='secondary' className='text-xs'>
                  {service}
                </Badge>
              ))}
              {order.services.length > 2 && (
                <Badge variant='secondary' className='text-xs'>
                  +{order.services.length - 2}
                </Badge>
              )}
            </div>
          )}

          {preferences.columnVisibility.assignedTech && order.assignedTech && (
            <div className='flex items-center gap-2 text-xs text-gray-500'>
              <span className='font-semibold'>
                {REPAIR_ORDER_LABELS.ASSIGNED_TECHNICIAN}
              </span>
              <span>{order.assignedTech.name}</span>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
