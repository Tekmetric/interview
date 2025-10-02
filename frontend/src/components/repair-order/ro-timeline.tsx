import { type ReactNode } from 'react'

import type { RepairOrder } from '@shared/types'
import { STAGE_ORDER } from '@shared/transitions'
import { KANBAN_LABELS } from '@shared/constants'
import { STATUS_ICONS, TIMELINE_STATUS_COLORS } from './ro-constants'

type TimelineEvent = {
  label: string
  date: Date | null
  color: string
  iconBg: string
  icon: ReactNode
  isActive: boolean
}

export function ROTimeline({ order }: { order: RepairOrder }) {
  const currentStatusIndex = STAGE_ORDER.indexOf(order.status)

  const events: TimelineEvent[] = STAGE_ORDER.map((status, index) => {
    const isActive = index <= currentStatusIndex
    const isPast = index < currentStatusIndex
    const isCurrent = index === currentStatusIndex
    const colors = TIMELINE_STATUS_COLORS[status]
    const Icon = STATUS_ICONS[status]

    return {
      label: KANBAN_LABELS.STATUS[status],
      date: isCurrent
        ? new Date(order.updatedAt)
        : isPast
          ? new Date(order.createdAt)
          : null,
      color: isActive ? colors.bg : 'bg-gray-300',
      iconBg: isActive ? colors.iconBg : 'bg-gray-300',
      icon: <Icon className='h-4 w-4' />,
      isActive,
    }
  })

  return (
    <div className='px-4 py-4'>
      <div className='relative'>
        <div className='absolute top-4 right-0 left-0 h-1 bg-gray-200' />

        <div className='relative flex justify-between'>
          {events.map((event, idx) => {
            const isLast = idx === events.length - 1
            const isFirst = idx === 0
            return (
              <div
                key={idx}
                className={`flex items-center gap-2 ${isLast ? 'flex-row-reverse' : ''}`}
              >
                <div
                  className={`relative z-10 flex h-8 w-8 shrink-0 items-center justify-center rounded-full ${event.iconBg} ${event.isActive ? 'text-white' : 'text-gray-500'}`}
                >
                  {event.icon}
                </div>
                <div
                  className={`flex flex-col gap-1 ${isLast ? 'items-end' : isFirst ? 'items-start' : 'items-center'}`}
                >
                  <p
                    className={`text-xs font-semibold ${event.isActive ? 'text-gray-900' : 'text-gray-400'}`}
                  >
                    {event.label}
                  </p>
                  {event.date && (
                    <p
                      className={`text-xs font-medium ${event.isActive ? 'text-gray-700' : 'text-gray-400'}`}
                    >
                      {event.date.toLocaleDateString('en-US', {
                        month: 'short',
                        day: 'numeric',
                      })}
                    </p>
                  )}
                </div>
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}
