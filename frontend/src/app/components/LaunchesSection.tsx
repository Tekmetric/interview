import dynamic from 'next/dynamic'

import { getUpcomingLaunches, getPastLaunches } from '@/app/lib/api'
import type { Launch } from '@/app/types'

const DynamicUpcomingLaunches = dynamic(
  () => import('./UpcomingLaunches/UpcomingLaunches'),
  {
    loading: () => (
      <div className="animate-pulse h-80 bg-muted rounded-lg"></div>
    ),
  }
)

const DynamicPastLaunches = dynamic(
  () => import('./PastLaunches/PastLaunches'),
  {
    loading: () => (
      <div className="animate-pulse h-80 bg-muted rounded-lg"></div>
    ),
  }
)

async function LaunchesSection(): Promise<React.ReactElement> {
  const [upcomingLaunchesData, pastLaunchesData] = (await Promise.all([
    getUpcomingLaunches(),
    getPastLaunches(1, 5),
  ])) as [Launch[], Launch[]]

  return (
    <>
      <DynamicUpcomingLaunches
        initialUpcomingLaunches={upcomingLaunchesData}
        data-testid="dynamic-upcoming-launches"
      />
      <DynamicPastLaunches
        initialData={pastLaunchesData}
        data-testid="dynamic-past-launches"
      />
    </>
  )
}

export default LaunchesSection
