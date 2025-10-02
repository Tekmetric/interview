import { Calendar } from 'lucide-react'
import type { RepairOrder } from '@shared/types'
import { REPAIR_ORDER_LABELS } from '@shared/constants'

type TimelineEvent = {
  label: string
  date: Date
  color: string
}

export function ROTimeline({ order }: { order: RepairOrder }) {
  const events: TimelineEvent[] = [
    { label: REPAIR_ORDER_LABELS.CREATED, date: new Date(order.createdAt), color: 'bg-blue-500' },
    {
      label: REPAIR_ORDER_LABELS.LAST_UPDATED,
      date: new Date(order.updatedAt),
      color: 'bg-orange-500',
    },
    ...(order.dueTime
      ? [{ label: REPAIR_ORDER_LABELS.DUE, date: new Date(order.dueTime), color: 'bg-green-500' }]
      : []),
  ]

  return (
    <div className='space-y-2 rounded-lg border border-gray-200 bg-gray-50 p-4'>
      <h3 className='text-xs font-semibold uppercase text-gray-500'>
        {REPAIR_ORDER_LABELS.TIMELINE}
      </h3>

      {/* Horizontal timeline bar */}
      <div className='relative pt-8'>
        {/* Progress line */}
        <div className='absolute top-12 left-0 right-0 h-1 bg-gray-200' />

        {/* Event markers */}
        <div className='relative flex justify-between'>
          {events.map((event, idx) => (
            <div key={idx} className='flex flex-col items-center'>
              {/* Label above */}
              <div className='absolute -top-6 text-center'>
                <p className='text-xs font-medium text-gray-700'>{event.label}</p>
              </div>

              {/* Icon circle */}
              <div
                className={`relative z-10 flex h-8 w-8 items-center justify-center rounded-full ${event.color}`}
              >
                <Calendar className='h-4 w-4 text-white' />
              </div>

              {/* Date below */}
              <div className='mt-2 text-center'>
                <p className='text-xs text-gray-600'>
                  {event.date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
