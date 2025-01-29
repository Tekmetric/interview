import LaunchList from '../LaunchList'

export default {
  title: 'Components/UpcomingLaunchList',
  component: LaunchList,
}

const launchesExample = [
  {
    id: '1',
    name: 'Upcoming Launch 1',
    date_utc: new Date(Date.now() + 86400000).toISOString(),
    date_unix: Math.floor((Date.now() + 86400000) / 1000),
  },
  {
    id: '2',
    name: 'Upcoming Launch 2',
    date_utc: new Date(Date.now() + 172800000).toISOString(),
    date_unix: Math.floor((Date.now() + 172800000) / 1000),
  },
]

export const Default = () => <LaunchList launches={launchesExample} />
