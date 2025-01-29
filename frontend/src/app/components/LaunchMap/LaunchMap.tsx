'use client'

import dynamic from 'next/dynamic'
import { memo, useEffect, useState } from 'react'

import { LAUNCH_SITES, MAP_CONFIG } from '@/app/lib/constants'
import type { LaunchSite } from '@/app/types'

import MapLoadingPlaceholder from './MapLoadingPlaceholder'

const MAP_HEIGHT = 400

const DynamicMap = dynamic(() => import('./Map'), {
  ssr: false,
  loading: () => (
    <MapLoadingPlaceholder
      height={MAP_HEIGHT}
      data-testid="map-loading-placeholder"
    />
  ),
})

function LaunchMap(): React.ReactElement {
  const [isClient, setIsClient] = useState(false)
  const [launchSites, setLaunchSites] = useState<LaunchSite[]>([])

  const mapConfig = {
    center: {
      lat: MAP_CONFIG.DEFAULT_CENTER[0],
      lng: MAP_CONFIG.DEFAULT_CENTER[1],
    },
    zoom: MAP_CONFIG.DEFAULT_ZOOM,
  }

  useEffect(() => {
    setIsClient(true)
    setLaunchSites([...LAUNCH_SITES])
  }, [])

  if (!isClient) {
    return <MapLoadingPlaceholder height={MAP_HEIGHT} />
  }

  return (
    <div aria-label="Launch site map" data-testid="launch-site-map">
      <DynamicMap
        launchSites={launchSites}
        mapHeight={MAP_HEIGHT}
        mapConfig={mapConfig}
      />
    </div>
  )
}

export default memo(LaunchMap)
