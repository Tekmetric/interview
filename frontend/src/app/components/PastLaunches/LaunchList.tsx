import { memo } from 'react'

import { formatDate } from '@/app/lib/utils'
import type { Launch } from '@/app/types'

interface LaunchListProps {
  launches: Launch[]
  hasMoreToLoad: boolean
  isLoadingMore: boolean
  onLoadMore: () => void
}

function LaunchList({
  launches,
  hasMoreToLoad,
  isLoadingMore,
  onLoadMore,
}: LaunchListProps): React.ReactElement {
  if (launches.length === 0) {
    return (
      <p
        className="text-center text-muted-foreground mt-4"
        data-testid="no-launches-message"
      >
        No launches found
      </p>
    )
  }

  return (
    <>
      <ul
        className="space-y-4 mt-6"
        aria-label="List of past launches"
        data-testid="launch-list"
      >
        {launches.map((launch: Launch) => (
          <LaunchItem key={launch.id} launch={launch} />
        ))}
      </ul>
      {hasMoreToLoad && (
        <button
          className="mt-6 btn btn-primary w-full focus:ring-2 focus:ring-primary focus:ring-offset-2 focus:ring-offset-background"
          onClick={onLoadMore}
          disabled={isLoadingMore}
          data-testid="load-more-button"
        >
          {isLoadingMore ? 'Loading...' : 'Load More'}
        </button>
      )}
      {!hasMoreToLoad && (
        <p
          className="mt-6 text-center text-muted-foreground"
          data-testid="all-launches-loaded"
        >
          All launches loaded
        </p>
      )}
    </>
  )
}

interface LaunchItemProps {
  launch: Launch
}

const LaunchItem = memo(function LaunchItem({
  launch,
}: LaunchItemProps): React.ReactElement {
  return (
    <li
      className="border-b border-border pb-4"
      data-testid={`launch-item-${launch.id}`}
    >
      <h3
        className="text-lg font-semibold text-foreground mb-1"
        data-testid="launch-name"
      >
        {launch.name}
      </h3>
      <p
        className="text-base text-muted-foreground mb-1"
        data-testid="launch-date"
      >
        {formatDate(launch.date_utc)}
      </p>
      <p className="text-base text-muted-foreground">
        Outcome:{' '}
        <span
          className={launch.success ? 'text-green-400' : 'text-red-400'}
          data-testid="launch-outcome"
        >
          {launch.success ? 'Success' : 'Failure'}
        </span>
      </p>
    </li>
  )
})

LaunchItem.displayName = 'LaunchItem'

export default memo(LaunchList)
