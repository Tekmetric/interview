'use client'

import { memo } from 'react'

import { formatDate } from '@/app/lib/utils'
import type { Launch } from '@/app/types'

interface LaunchListProps {
  launches: Launch[]
}

function LaunchList({ launches }: LaunchListProps): React.ReactElement {
  if (launches.length === 0) {
    return (
      <p className="text-muted-foreground" data-testid="no-upcoming-launches">
        No upcoming launches scheduled.
      </p>
    )
  }

  return (
    <ul
      className="space-y-4"
      aria-label="List of upcoming launches"
      data-testid="upcoming-launches-list"
    >
      {launches.map((launch: Launch) => (
        <li
          key={launch.id}
          className="border-b border-border pb-4"
          data-testid={`launch-item-${launch.id}`}
        >
          <h3
            className="font-medium text-foreground mb-1"
            data-testid={`launch-name-${launch.id}`}
          >
            {launch.name}
          </h3>
          <p
            className="text-sm text-muted-foreground"
            data-testid={`launch-date-${launch.id}`}
          >
            {formatDate(launch.date_utc)}
          </p>
        </li>
      ))}
    </ul>
  )
}

export default memo(LaunchList)
