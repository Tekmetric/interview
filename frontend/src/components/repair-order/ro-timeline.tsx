import * as React from 'react'
import { PlayCircle, Clock, Flag } from 'lucide-react'
import type { RepairOrder } from '@shared/types'

type TimelineEvent = {
  label: string
  date: Date
  color: string
  icon: React.ReactNode
}

export function ROTimeline({ order }: { order: RepairOrder }) {
  const events: TimelineEvent[] = [
    {
      label: 'Started',
      date: new Date(order.createdAt),
      color: 'bg-blue-500',
      icon: <PlayCircle className='h-4 w-4 text-white' />,
    },
    {
      label: 'In Progress',
      date: new Date(order.updatedAt),
      color: 'bg-orange-500',
      icon: <Clock className='h-4 w-4 text-white' />,
    },
    ...(order.dueTime
      ? [
          {
            label: 'Due Date',
            date: new Date(order.dueTime),
            color: 'bg-green-500',
            icon: <Flag className='h-4 w-4 text-white' />,
          },
        ]
      : []),
  ]

  return (
    <div className='px-4 py-4'>
      <div className='relative'>
        <div className='absolute top-4 right-0 left-0 h-1 bg-gray-200' />

        <div className='relative flex justify-between'>
          {events.map((event, idx) => {
            const isLast = idx === events.length - 1
            return (
              <div
                key={idx}
                className={`flex items-center gap-2 ${isLast ? 'flex-row-reverse' : ''}`}
              >
                <div
                  className={`relative z-10 flex h-8 w-8 shrink-0 items-center justify-center rounded-full ${event.color}`}
                >
                  {event.icon}
                </div>
                <div className={`flex flex-col gap-1 ${isLast ? 'items-end' : ''}`}>
                  <p className='text-xs font-semibold text-gray-700'>{event.label}</p>
                  <p className='text-xs font-medium text-gray-900'>
                    {event.date.toLocaleDateString('en-US', {
                      month: 'short',
                      day: 'numeric',
                    })}
                  </p>
                </div>
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}
