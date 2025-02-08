'use client'

import { useState, useEffect } from 'react'
import { formatDate } from '@/app/lib/utils'
import type { Launch, Launchpad } from '@/app/types'

interface LaunchInfoProps {
  launch: Launch
  launchpad: Launchpad | null
}

export function LaunchInfo({
  launch,
  launchpad,
}: LaunchInfoProps): React.ReactElement {
  const [currentLaunch, setCurrentLaunch] = useState<Launch>(launch)
  const [currentLaunchpad, setCurrentLaunchpad] = useState<Launchpad | null>(
    launchpad
  )

  useEffect(() => {
    setCurrentLaunch(launch)
  }, [launch])

  useEffect(() => {
    setCurrentLaunchpad(launchpad)
  }, [launchpad])

  return (
    <div className="space-y-6" data-testid="launch-info">
      <div
        className="grid grid-cols-1 md:grid-cols-4 gap-4"
        data-testid="launch-info-grid"
      >
        <div className="md:col-span-1" data-testid="launch-mission">
          <h3 className="text-xl font-semibold mb-2">Mission</h3>
          <p
            className="text-muted-foreground"
            data-testid="launch-mission-name"
          >
            {currentLaunch.name}
          </p>
        </div>

        <div className="md:col-span-2" data-testid="launch-site">
          <h3 className="text-xl font-semibold mb-2">Launch Site</h3>
          {currentLaunchpad ? (
            <p
              className="text-muted-foreground"
              data-testid="launchpad-full-name"
            >
              {currentLaunchpad.full_name}
            </p>
          ) : (
            <p
              className="text-muted-foreground animate-pulse"
              data-testid="loading-launch-site"
            >
              Loading launch site...
            </p>
          )}
        </div>

        <div className="md:col-span-1" data-testid="launch-date">
          <h3 className="text-xl font-semibold mb-2">Launch Date</h3>
          <p className="text-muted-foreground" data-testid="launch-date-value">
            {formatDate(currentLaunch.date_utc)}
          </p>
        </div>
      </div>

      {currentLaunch.links?.webcast && (
        <div className="mt-6" data-testid="webcast-link">
          <a
            href={currentLaunch.links.webcast}
            target="_blank"
            rel="noopener noreferrer"
            className="btn btn-primary inline-block transition-transform duration-200 hover:scale-105 focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2 focus:ring-offset-background"
            data-testid="watch-webcast-button"
          >
            Watch Webcast
          </a>
        </div>
      )}
    </div>
  )
}
