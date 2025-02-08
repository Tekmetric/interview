'use client'

import { memo, useMemo } from 'react'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts'

import type { LaunchStats } from '@/app/types'

interface LaunchStatsChartProps {
  data: LaunchStats[]
}

function LaunchStatsChart({ data }: LaunchStatsChartProps): React.ReactElement {
  const chartData = useMemo(() => {
    return data.map((item) => ({
      year: item.year,
      successful: item.successful,
      failed: item.failed,
    }))
  }, [data])

  return (
    <ResponsiveContainer
      width="100%"
      height={400}
      data-testid="launch-stats-responsive-container"
    >
      <BarChart
        data={chartData}
        margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
        data-testid="launch-stats-bar-chart"
      >
        <CartesianGrid
          strokeDasharray="3 3"
          className="stroke-muted"
          data-testid="launch-stats-cartesian-grid"
        />
        <XAxis
          dataKey="year"
          className="text-foreground"
          data-testid="launch-stats-x-axis"
        />
        <YAxis className="text-foreground" data-testid="launch-stats-y-axis" />
        <Tooltip
          contentStyle={{
            backgroundColor: 'hsl(var(--background))',
            border: '1px solid hsl(var(--border))',
            borderRadius: '0.375rem',
          }}
          data-testid="launch-stats-tooltip"
        />
        <Legend data-testid="launch-stats-legend" />
        <Bar
          dataKey="successful"
          name="Successful Launches"
          fill="hsl(var(--primary))"
          data-testid="launch-stats-bar-successful"
        />
        <Bar
          dataKey="failed"
          name="Failed Launches"
          fill="#f54029"
          data-testid="launch-stats-bar-failed"
        />
      </BarChart>
    </ResponsiveContainer>
  )
}

export default memo(LaunchStatsChart)
