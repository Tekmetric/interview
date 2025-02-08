'use client'

import { Map as LeafletMap, TileLayer, Marker } from 'leaflet'
import { useEffect, useRef } from 'react'
import { createRocketIcon } from '@/app/lib/rocketIcon'

import 'leaflet/dist/leaflet.css'
import type { LaunchSite } from '../../types'

interface MapProps {
  launchSites: LaunchSite[]
  mapHeight: number
  mapConfig: {
    center: {
      lat: number
      lng: number
    }
    zoom: number
  }
}

function Map({
  launchSites,
  mapHeight,
  mapConfig,
}: MapProps): React.ReactElement {
  const mapRef = useRef<LeafletMap | null>(null)
  const mapContainerRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (
      typeof window === 'undefined' ||
      !mapContainerRef.current ||
      mapRef.current
    )
      return

    mapRef.current = new LeafletMap(mapContainerRef.current).setView(
      [mapConfig.center.lat, mapConfig.center.lng],
      mapConfig.zoom
    )

    new TileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      maxZoom: 19,
    }).addTo(mapRef.current)

    const rocketIcon = createRocketIcon()

    launchSites.forEach((site) => {
      if (mapRef.current) {
        new Marker([site.latitude, site.longitude], { icon: rocketIcon })
          .addTo(mapRef.current)
          .bindPopup(site.name)
      }
    })

    return () => {
      if (mapRef.current) {
        mapRef.current.remove()
        mapRef.current = null
      }
    }
  }, [launchSites, mapConfig])

  return (
    <div
      ref={mapContainerRef}
      style={{ height: `${mapHeight}px`, width: '100%' }}
      aria-label="Interactive map of launch sites"
      data-testid="launch-map"
    />
  )
}

export default Map
