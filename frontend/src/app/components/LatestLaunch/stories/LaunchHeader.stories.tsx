import LaunchHeader from '../LaunchHeader'

export default {
  title: 'Components/LaunchHeader',
  component: LaunchHeader,
}

const launchExample = {
  id: '1',
  name: 'Test Launch',
  date_utc: new Date().toISOString(),
  date_unix: Math.floor(new Date().getTime() / 1000),
  success: true,
  links: {
    patch: {
      small: '/images/patch.png',
    },
  },
}

export const Default = () => <LaunchHeader launch={launchExample} />
