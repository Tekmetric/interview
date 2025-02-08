import LaunchStats from '../LaunchStats'

export default {
  title: 'Components/LaunchStats',
  component: LaunchStats,
}

const launchStatsExample = [
  { year: 2020, successful: 5, failed: 1 },
  { year: 2021, successful: 7, failed: 2 },
  { year: 2022, successful: 6, failed: 3 },
]

export const Default = () => <LaunchStats launchStats={launchStatsExample} />
