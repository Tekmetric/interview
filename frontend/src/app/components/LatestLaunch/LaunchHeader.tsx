import Image from 'next/image'

import { formatDate } from '@/app/lib/utils'
import type { Launch } from '@/app/types'

interface LaunchHeaderProps {
  launch: Launch
}

function LaunchHeader({ launch }: LaunchHeaderProps): React.ReactElement {
  return (
    <div className="w-full md:w-1/3 text-center" data-testid="launch-header">
      {launch.links?.patch?.small && (
        <div
          className="relative w-full max-w-[200px] aspect-square mx-auto mb-4"
          data-testid="mission-patch"
        >
          <Image
            src={launch.links.patch.small || '/images/placeholder.svg'}
            alt={`Mission patch for ${launch.name}`}
            fill
            sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
            className="object-contain"
            priority
          />
        </div>
      )}
      <div className="space-y-2" data-testid="launch-details-header">
        <h3
          className="text-xl font-semibold text-foreground"
          data-testid="launch-name"
        >
          {launch.name}
        </h3>
        <p className="text-sm text-muted-foreground" data-testid="launch-date">
          {formatDate(launch.date_utc)}
        </p>
        <div
          className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-sm ${
            launch.success
              ? 'bg-green-900/50 text-green-300'
              : 'bg-red-900/50 text-red-300'
          }`}
          data-testid="launch-status"
        >
          {launch.success ? 'Successful' : 'Failed'}
        </div>
      </div>
    </div>
  )
}

export default LaunchHeader
