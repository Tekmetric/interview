import LaunchMap from '../LaunchMap'

export default {
  title: 'Components/LaunchMap',
  component: LaunchMap,
}

const launchSitesExample = [
  { name: 'Launch Site 1', latitude: 34.0522, longitude: -118.2437 },
  { name: 'Launch Site 2', latitude: 36.7783, longitude: -119.4179 },
]

const mapConfigExample = {
  center: { lat: 34.0522, lng: -118.2437 },
  zoom: 5,
}

const LaunchMapWithProps = LaunchMap as unknown as React.FC<{
  launchSites: { name: string; latitude: number; longitude: number }[]
  mapConfig: { center: { lat: number; lng: number }; zoom: number }
}>

export const Default = () => {
  return (
    <LaunchMapWithProps
      launchSites={launchSitesExample}
      mapConfig={mapConfigExample}
    />
  )
}
