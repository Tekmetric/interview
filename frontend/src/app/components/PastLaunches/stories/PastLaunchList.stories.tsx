import LaunchList from '../LaunchList'

export default {
  title: 'Components/PastLaunchList',
  component: LaunchList,
}

const launchesExample = [
  {
    id: '1',
    name: 'Launch 1',
    date_utc: new Date().toISOString(),
    date_unix: Math.floor(new Date().getTime() / 1000),
    success: true,
  },
  {
    id: '2',
    name: 'Launch 2',
    date_utc: new Date().toISOString(),
    date_unix: Math.floor(new Date().getTime() / 1000),
    success: false,
  },
]

export const Default = () => (
  <LaunchList
    launches={launchesExample}
    hasMoreToLoad={true}
    isLoadingMore={false}
    onLoadMore={() => console.log('Load more launches')}
  />
)
