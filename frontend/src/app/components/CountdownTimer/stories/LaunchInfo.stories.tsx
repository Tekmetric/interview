import { LaunchInfo } from '../LaunchInfo'

export default {
  title: 'Components/LaunchInfo',
  component: LaunchInfo,
}

const launchExample = {
  id: '1',
  name: 'Test Launch',
  date_utc: new Date().toISOString(),
  date_unix: Math.floor(new Date().getTime() / 1000),
  links: {
    webcast: 'https://example.com/webcast',
  },
}

const launchpadExample = {
  id: '1',
  full_name: 'Test Launchpad',
  latitude: 28.5721,
  longitude: -80.648,
}

export const Default = () => (
  <LaunchInfo
    launch={launchExample}
    launchpad={{ ...launchpadExample, name: 'Test Launchpad' }}
  />
)
