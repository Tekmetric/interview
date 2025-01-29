'use client'

import { motion } from 'framer-motion'
import { useState, useMemo, useCallback, memo } from 'react'

import { LoadingSkeleton } from '@/app/components/ui/loading-skeleton'
import { useCustomDate } from '@/app/hooks/useCustomDate'
import { usePastLaunches } from '@/app/hooks/useSpaceXData'
import type { Launch } from '@/app/types'

import LaunchFilters from './LaunchFilters'
import LaunchList from './LaunchList'

interface PastLaunchesProps {
  initialData: Launch[]
}

function PastLaunches({ initialData }: PastLaunchesProps): React.ReactElement {
  const { data, size, setSize, isValidating, error } =
    usePastLaunches(initialData)
  const [filters, setFilters] = useState({
    success: 'all',
    rocket: 'all',
    dateRange: { start: '', end: '' },
  })
  const [currentSort, setCurrentSort] = useState('date_desc')
  const customDate = useCustomDate()
  const [displayCount, setDisplayCount] = useState(5)

  const launches = useMemo(() => {
    if (!data) return []
    const allLaunches = ([] as Launch[]).concat(...(data as Launch[][]))
    return allLaunches.filter(
      (launch, index, self) =>
        index === self.findIndex((t) => t.id === launch.id)
    )
  }, [data])

  const filteredLaunches = useMemo(() => {
    if (!launches) return []

    let result = launches.filter(
      (launch: Launch) => new Date(launch.date_utc) <= new Date(customDate)
    )

    if (filters.success !== 'all') {
      result = result.filter(
        (launch: Launch) => launch.success?.toString() === filters.success
      )
    }
    if (filters.rocket !== 'all') {
      result = result.filter(
        (launch: Launch) => launch.rocket === filters.rocket
      )
    }
    if (filters.dateRange.start) {
      result = result.filter(
        (launch: Launch) =>
          new Date(launch.date_utc) >= new Date(filters.dateRange.start)
      )
    }
    if (filters.dateRange.end) {
      result = result.filter(
        (launch: Launch) =>
          new Date(launch.date_utc) <= new Date(filters.dateRange.end)
      )
    }

    return result.sort((a: Launch, b: Launch) => {
      if (currentSort === 'name_asc') {
        return a.name.localeCompare(b.name)
      } else if (currentSort === 'name_desc') {
        return b.name.localeCompare(a.name)
      } else {
        const dateA = new Date(a.date_utc).getTime()
        const dateB = new Date(b.date_utc).getTime()
        return currentSort === 'date_desc' ? dateB - dateA : dateA - dateB
      }
    })
  }, [launches, customDate, filters, currentSort])

  const displayedLaunches = filteredLaunches.slice(0, displayCount)

  const isLoadingMore = isValidating && size > 1
  const hasMoreToLoad = displayedLaunches.length < filteredLaunches.length

  const loadMore = useCallback(() => {
    if (!isLoadingMore && hasMoreToLoad) {
      setDisplayCount((prevCount) => prevCount + 5)
      if (displayCount + 5 > launches.length) {
        setSize((prevSize) => prevSize + 1)
      }
    }
  }, [isLoadingMore, hasMoreToLoad, displayCount, launches.length, setSize])

  const handleFilterChange = useCallback(
    (newFilters: typeof filters) => {
      setFilters(newFilters)
      setDisplayCount((prev) => (prev < 5 ? 5 : prev))
      setSize(1)
    },
    [setSize]
  )

  const handleSortChange = useCallback(
    (sort: string) => {
      setCurrentSort(sort)
      setDisplayCount((prev) => (prev < 5 ? 5 : prev))
      setSize(1)
    },
    [setSize]
  )

  if (error)
    return (
      <div className="card p-6 text-destructive">
        Failed to load past launches
      </div>
    )
  if (!data) return <LoadingSkeleton className="h-96" />

  return (
    <motion.section
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 1 }}
      className="card mb-8"
      data-testid="past-launches-section"
    >
      <h2 className="section-title mb-6" data-testid="past-launches-title">
        Past Launches
      </h2>
      <LaunchFilters
        onFilterChange={handleFilterChange}
        onSortChange={handleSortChange}
        data-testid="launch-filters"
      />
      <LaunchList
        launches={displayedLaunches}
        hasMoreToLoad={hasMoreToLoad}
        isLoadingMore={isLoadingMore}
        onLoadMore={loadMore}
        data-testid="launch-list"
      />
    </motion.section>
  )
}

export default memo(PastLaunches)
