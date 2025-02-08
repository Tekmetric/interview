import type { LaunchStats as LaunchStatsType } from '@/app/types'
import { getLaunchStats } from '@/app/lib/api'

import LaunchStats from './LaunchStats/LaunchStats'

async function LaunchStatsSection(): Promise<React.ReactElement> {
  const launchStatsData = (await getLaunchStats()) as LaunchStatsType[]

  return (
    <LaunchStats launchStats={launchStatsData} data-testid="launch-stats" />
  )
}

export default LaunchStatsSection
