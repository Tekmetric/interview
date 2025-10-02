import { useState, useEffect, useCallback, useRef } from 'react'
import { getItem, setItem } from '@/lib/storage'

export function usePersistedState<T>(
  key: string,
  defaultValue: T,
): [T, (value: T | ((prev: T) => T)) => void] {
  const [state, setState] = useState<T>(() => getItem(key, defaultValue))
  const isMountedRef = useRef(false)

  // Sync state to localStorage (debounced)
  useEffect(() => {
    // Skip first render to avoid writing default value immediately
    if (!isMountedRef.current) {
      isMountedRef.current = true
      return
    }

    setItem(key, state, true)
  }, [key, state])

  const updateState = useCallback((value: T | ((prev: T) => T)) => {
    setState(value)
  }, [])

  return [state, updateState]
}
