import dynamic from 'next/dynamic'

import { getNextLaunch } from '@/app/lib/api'
import type { Launch } from '@/app/types'

const DynamicCountdownTimer = dynamic(
  () => import('./CountdownTimer/CountdownTimer'),
  {
    loading: () => (
      <div className="animate-pulse h-40 bg-muted rounded-lg"></div>
    ),
  }
)

async function NextLaunchSection(): Promise<React.ReactElement> {
  const nextLaunchData = await getNextLaunch()

  return (
    <DynamicCountdownTimer
      initialData={nextLaunchData as Launch}
      data-testid="next-launch-timer"
    />
  )
}

export default NextLaunchSection
