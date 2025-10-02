import { debounce as debounceFn } from './debounce'

const STORAGE_VERSION = 1
const DEBOUNCE_MS = 500

interface StorageSchema<T> {
  version: number
  data: T
  lastUpdated: string
}

function isBrowser(): boolean {
  return typeof window !== 'undefined'
}

export function getItem<T>(key: string, defaultValue: T): T {
  if (!isBrowser()) {
    return defaultValue
  }

  try {
    const item = window.localStorage.getItem(key)
    if (!item) {
      return defaultValue
    }

    const parsed = JSON.parse(item) as StorageSchema<T>

    // Version mismatch - return default and clear old data
    if (parsed.version !== STORAGE_VERSION) {
      window.localStorage.removeItem(key)
      return defaultValue
    }

    return parsed.data
  } catch (error) {
    console.error(`Error reading from localStorage key "${key}":`, error)
    return defaultValue
  }
}

export function setItem<T>(key: string, value: T, debounce = true): void {
  if (!isBrowser()) {
    return
  }

  const write = () => {
    try {
      const schema: StorageSchema<T> = {
        version: STORAGE_VERSION,
        data: value,
        lastUpdated: new Date().toISOString(),
      }
      window.localStorage.setItem(key, JSON.stringify(schema))
    } catch (error) {
      console.error(`Error writing to localStorage key "${key}":`, error)
    }
  }

  if (debounce) {
    debounceFn(key, write, DEBOUNCE_MS)
  } else {
    write()
  }
}

export function removeItem(key: string): void {
  if (!isBrowser()) {
    return
  }

  try {
    window.localStorage.removeItem(key)
  } catch (error) {
    console.error(`Error removing localStorage key "${key}":`, error)
  }
}

export function clearAll(): void {
  if (!isBrowser()) {
    return
  }

  try {
    window.localStorage.clear()
  } catch (error) {
    console.error('Error clearing localStorage:', error)
  }
}
