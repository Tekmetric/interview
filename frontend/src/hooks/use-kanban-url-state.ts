import { useEffect, useState } from 'react'
import { useLocation, useSearch } from 'wouter'
import type { Filter } from '@/components/ui/filters'
import {
  parseFilterFromUrl,
  parseSearchFromUrl,
  buildUrlWithSearch,
} from '@/lib/filter-utils'

type UseKanbanUrlStateReturn = {
  searchParams: URLSearchParams
  initializeFromUrl: (
    setFilters: (filters: Filter[]) => void,
    setSearchQuery: (query: string) => void,
  ) => void
  syncSearchToUrl: (searchQuery: string) => void
}

/**
 * Business logic for URL state management in Kanban view.
 * Handles parsing URL parameters on mount and syncing state changes back to URL.
 */
export function useKanbanUrlState(): UseKanbanUrlStateReturn {
  const [, setLocation] = useLocation()
  const searchParams = new URLSearchParams(useSearch())
  const [hasInitialized, setHasInitialized] = useState(false)

  // Parse filters and search from URL on mount
  const initializeFromUrl = (
    setFilters: (filters: Filter[]) => void,
    setSearchQuery: (query: string) => void,
  ) => {
    if (hasInitialized) return

    const filterFromUrl = parseFilterFromUrl(searchParams)
    if (filterFromUrl) {
      setFilters([filterFromUrl])
    }

    const searchFromUrl = parseSearchFromUrl(searchParams)
    if (searchFromUrl) {
      setSearchQuery(searchFromUrl)
    }

    setHasInitialized(true)
  }

  // Sync search query to URL with debouncing
  const syncSearchToUrl = (searchQuery: string) => {
    const timeoutId = setTimeout(() => {
      const urlParams = buildUrlWithSearch(searchParams, searchQuery)
      setLocation(`?${urlParams}`, { replace: true })
    }, 500) // Debounce to avoid too many URL updates

    return () => clearTimeout(timeoutId)
  }

  return {
    searchParams,
    initializeFromUrl,
    syncSearchToUrl,
  }
}

/**
 * Hook to automatically sync search query to URL.
 * Handles debouncing internally.
 */
export function useSyncSearchToUrl(searchQuery: string) {
  const { syncSearchToUrl } = useKanbanUrlState()

  useEffect(() => {
    const cleanup = syncSearchToUrl(searchQuery)
    return cleanup
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchQuery])
}
