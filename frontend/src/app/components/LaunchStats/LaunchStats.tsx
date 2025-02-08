import { memo } from 'react'

import type { LaunchStats } from '@/app/types'

import LaunchStatsChart from './LaunchStatsChart'

interface LaunchStatsProps {
  launchStats: LaunchStats[]
}

function LaunchStats({ launchStats }: LaunchStatsProps): React.ReactElement {
  return (
    <section
      className="card mb-8"
      aria-labelledby="launch-stats-title"
      data-testid="launch-stats-section"
    >
      <h2 id="launch-stats-title" className="section-title mb-6">
        Launch Statistics
      </h2>
      <LaunchStatsChart data={launchStats} data-testid="launch-stats-chart" />
    </section>
  )
}

export default memo(LaunchStats)
