import Map from '../Map'

export default {
  title: 'Components/Map',
  component: Map,
}

const launchSitesExample = [
  { id: '1', name: 'Launch Site 1', latitude: 34.0522, longitude: -118.2437 },
  { id: '2', name: 'Launch Site 2', latitude: 36.7783, longitude: -119.4179 },
]

const mapConfigExample = {
  center: { lat: 34.0522, lng: -118.2437 },
  zoom: 5,
}

export const Default = () => (
  <Map
    launchSites={launchSitesExample}
    mapHeight={400}
    mapConfig={mapConfigExample}
  />
)
