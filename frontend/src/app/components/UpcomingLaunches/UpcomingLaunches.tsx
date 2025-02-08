'use client'

import { motion } from 'framer-motion'
import { useState, useEffect, useMemo, memo } from 'react'

import { LoadingSkeleton } from '@/app/components/ui/loading-skeleton'
import { useCustomDate } from '@/app/hooks/useCustomDate'
import { useUpcomingLaunches } from '@/app/hooks/useSpaceXData'
import type { Launch } from '@/app/types'

import LaunchList from './LaunchList'

interface UpcomingLaunchesProps {
  initialUpcomingLaunches: Launch[]
}

function UpcomingLaunches({
  initialUpcomingLaunches,
}: UpcomingLaunchesProps): React.ReactNode {
  const { data: upcomingLaunches, error } = useUpcomingLaunches(
    initialUpcomingLaunches
  )
  const [filteredLaunches, setFilteredLaunches] = useState<Launch[]>([])
  const currentDate = useCustomDate()

  const filterAndSortLaunches = useMemo(() => {
    if (!upcomingLaunches || !Array.isArray(upcomingLaunches)) return []

    return upcomingLaunches
      .filter(
        (launch: Launch) => new Date(launch.date_utc) > new Date(currentDate)
      )
      .sort(
        (a: Launch, b: Launch) =>
          new Date(a.date_utc).getTime() - new Date(b.date_utc).getTime()
      )
      .slice(0, 5)
  }, [upcomingLaunches, currentDate])

  useEffect(() => {
    setFilteredLaunches(filterAndSortLaunches)
  }, [filterAndSortLaunches])

  if (error)
    return (
      <div className="card p-6 text-destructive">
        Failed to load upcoming launches
      </div>
    )
  if (!upcomingLaunches) return <LoadingSkeleton className="h-80" />

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 1 }}
      className="card mb-8"
      data-testid="upcoming-launches-container"
    >
      <h2 className="section-title mb-6" data-testid="upcoming-launches-title">
        Upcoming Launches
      </h2>
      <LaunchList launches={filteredLaunches} />
    </motion.div>
  )
}

export default memo(UpcomingLaunches)
