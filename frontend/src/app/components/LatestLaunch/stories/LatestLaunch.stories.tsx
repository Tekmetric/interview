import LatestLaunch from '../LatestLaunch'

export default {
  title: 'Components/LatestLaunch',
  component: LatestLaunch,
}

const launchExample = {
  id: '1',
  name: 'Test Launch',
  date_utc: new Date().toISOString(),
  date_unix: Math.floor(new Date().getTime() / 1000),
  details: 'This is a detailed description of the latest mission.',
  links: {
    youtube_id: 'dQw4w9WgXcQ',
  },
}

export const Default = () => <LatestLaunch latestLaunch={launchExample} />
