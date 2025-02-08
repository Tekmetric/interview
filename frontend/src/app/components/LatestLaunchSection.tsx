import { getLatestLaunch } from '@/app/lib/api'
import type { Launch } from '@/app/types'

import LatestLaunch from './LatestLaunch/LatestLaunch'

async function LatestLaunchSection(): Promise<React.ReactElement> {
  const latestLaunchData = (await getLatestLaunch()) as Launch

  return (
    <LatestLaunch latestLaunch={latestLaunchData} data-testid="latest-launch" />
  )
}

export default LatestLaunchSection
