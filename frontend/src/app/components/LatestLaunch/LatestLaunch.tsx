import { memo } from 'react'

import LaunchDetails from '@/app/components/LatestLaunch/LaunchDetails'
import LaunchHeader from '@/app/components/LatestLaunch/LaunchHeader'
import LaunchVideo from '@/app/components/LatestLaunch/LaunchVideo'
import LaunchMap from '@/app/components/LaunchMap/LaunchMap'
import { LoadingSkeleton } from '@/app/components/ui/loading-skeleton'
import type { Launch } from '@/app/types'

interface LatestLaunchProps {
  latestLaunch: Launch | null
}

function LatestLaunch({ latestLaunch }: LatestLaunchProps): React.ReactElement {
  if (!latestLaunch) {
    return <LoadingSkeleton className="h-96" data-testid="loading-skeleton" />
  }

  return (
    <div className="card mb-8" data-testid="latest-launch">
      <h2 className="section-title mb-6" data-testid="latest-launch-title">
        Latest Launch
      </h2>
      <div
        className="flex flex-col md:flex-row items-start gap-6"
        data-testid="latest-launch-content"
      >
        <LaunchHeader launch={latestLaunch} data-testid="launch-header" />
        <div className="flex-1 w-full" data-testid="launch-video-details">
          <LaunchVideo
            youtubeId={latestLaunch.links?.youtube_id}
            launchName={latestLaunch.name}
            data-testid="launch-video"
          />
          <LaunchDetails
            details={latestLaunch.details}
            data-testid="launch-details"
          />
        </div>
      </div>
      <div className="mt-8" data-testid="launch-location">
        <h3
          className="text-lg font-medium mb-4 text-foreground"
          data-testid="launch-location-title"
        >
          Launch Location
        </h3>
        <LaunchMap data-testid="launch-map" />
      </div>
    </div>
  )
}

export default memo(LatestLaunch)
