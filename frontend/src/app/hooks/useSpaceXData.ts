import useSWR from 'swr'
import type { SWRConfiguration } from 'swr'
import useSWRInfinite from 'swr/infinite'
import type { SWRInfiniteResponse } from 'swr/infinite'

import { Launch, Launchpad } from '../types'

/**
 * fetcher
 *
 * A generic fetcher function that retrieves data from the provided URL.
 * It throws an error if the HTTP response is not successful.
 *
 * @param {string} url - The URL to fetch data from.
 * @returns {Promise<T>} - A promise that resolves to the data of type T.
 */
export const fetcher = async <T>(url: string): Promise<T> => {
  const res = await fetch(url)
  if (!res.ok) {
    throw new Error('An error occurred while fetching the data.')
  }
  return res.json() as Promise<T>
}

const defaultConfig: SWRConfiguration = {
  revalidateOnFocus: false,
  revalidateOnReconnect: false,
  refreshInterval: 60000,
  shouldRetryOnError: true,
  errorRetryCount: 3,
}

/**
 * useNextLaunch
 *
 * This hook expects the API at "/api/nextLaunch" to return an object of type:
 * { nextLaunch: Launch; launchpad: Launchpad | null }
 *
 * The fallbackData for launchpad is set to null.
 *
 * @param {Launch} initialData - The initial launch data to use as fallback.
 * @returns An object containing the next launch, its launchpad details, and any error encountered.
 */
export function useNextLaunch(initialData: Launch): {
  data: Launch | null
  launchpad: Launchpad | null
  error: Error | null
} {
  const { data, error } = useSWR<{
    nextLaunch: Launch
    launchpad: Launchpad | null
  }>('/api/nextLaunch', fetcher, {
    refreshInterval: 30000,
    fallbackData: { nextLaunch: initialData, launchpad: null },
  })

  return {
    data: data?.nextLaunch ?? null,
    launchpad: data?.launchpad ?? null,
    error,
  }
}

/**
 * useLatestLaunch
 *
 * This hook fetches the latest launch data from the "/api/latestLaunch" endpoint.
 * It uses the provided initialData as fallback.
 *
 * @param {unknown} initialData - The initial data to use as fallback.
 * @returns The SWR response for the latest launch data.
 */
export function useLatestLaunch(
  initialData: unknown
): ReturnType<typeof useSWR> {
  return useSWR<unknown>('/api/latestLaunch', fetcher, {
    ...defaultConfig,
    fallbackData: initialData,
  })
}

/**
 * useUpcomingLaunches
 *
 * This hook fetches upcoming launches from the "/api/upcomingLaunches" endpoint.
 * The provided initialData serves as fallback data.
 *
 * @param {unknown} initialData - The initial data to use as fallback.
 * @returns The SWR response for upcoming launches.
 */
export function useUpcomingLaunches(
  initialData: unknown
): ReturnType<typeof useSWR> {
  return useSWR<unknown>('/api/upcomingLaunches', fetcher, {
    ...defaultConfig,
    fallbackData: initialData,
  })
}

/**
 * usePastLaunches
 *
 * Each page returns an array of Launch.
 * Therefore, the SWRInfinite generic type is set to Launch[],
 * and the data (an array of pages) will be of type Launch[][].
 *
 * The fallbackData is provided as an array containing the initial data.
 *
 * @param {Launch[]} initialData - The initial page of launches to use as fallback data.
 * @returns The SWRInfinite response for past launches.
 */
export function usePastLaunches(
  initialData: Launch[]
): SWRInfiniteResponse<Launch[], Error> {
  const getKey = (
    pageIndex: number,
    previousPageData: Launch[] | null
  ): string | null => {
    // If there is no more data, return null to stop fetching.
    if (previousPageData && previousPageData.length === 0) return null
    return `/api/pastLaunches?page=${pageIndex + 1}&limit=5`
  }

  return useSWRInfinite<Launch[]>(getKey, fetcher, {
    ...defaultConfig,
    fallbackData: initialData ? [initialData] : undefined,
    revalidateFirstPage: false,
    dedupingInterval: 60000,
  })
}

/**
 * useLaunchStats
 *
 * This hook fetches launch statistics from the "/api/launchStats" endpoint.
 * It uses the provided initialData as fallback.
 *
 * @param {unknown} initialData - The initial stats data to use as fallback.
 * @returns The SWR response for launch statistics.
 */
export function useLaunchStats(
  initialData: unknown
): ReturnType<typeof useSWR> {
  return useSWR<unknown>('/api/launchStats', fetcher, {
    ...defaultConfig,
    fallbackData: initialData,
  })
}

/**
 * useRockets
 *
 * This hook fetches the list of rockets from the "/api/rockets" endpoint.
 * It uses the provided initialData as fallback.
 *
 * @param {unknown} initialData - The initial rockets data to use as fallback.
 * @returns The SWR response for rockets.
 */
export function useRockets(initialData: unknown): ReturnType<typeof useSWR> {
  return useSWR<unknown>('/api/rockets', fetcher, {
    ...defaultConfig,
    fallbackData: initialData,
  })
}
