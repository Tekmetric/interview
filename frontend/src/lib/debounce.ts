type DebounceTimers = Record<string, ReturnType<typeof setTimeout>>
const timers: DebounceTimers = {}

/**
 * Debounces a function call by key. Multiple calls with the same key
 * will cancel previous timers and only execute the last call after the delay.
 *
 * @param key - Unique identifier for this debounced operation
 * @param fn - Function to execute after delay
 * @param delayMs - Delay in milliseconds (default: 500)
 *
 * @example
 * debounce('search', () => fetchResults(query), 300)
 */
export function debounce(key: string, fn: () => void, delayMs = 500): void {
  if (timers[key]) {
    clearTimeout(timers[key])
  }

  timers[key] = setTimeout(() => {
    fn()
    delete timers[key]
  }, delayMs)
}

/**
 * Immediately executes any pending debounced function for the given key
 * and cancels the timer.
 *
 * @param key - Unique identifier for the debounced operation
 *
 * @example
 * flushDebounce('search') // Executes search immediately
 */
export function flushDebounce(key: string): void {
  if (timers[key]) {
    clearTimeout(timers[key])
    delete timers[key]
  }
}

/**
 * Cancels all pending debounced functions and clears all timers.
 * Useful for cleanup on unmount or navigation.
 *
 * @example
 * clearAllDebounces() // Cancel all pending operations
 */
export function clearAllDebounces(): void {
  Object.keys(timers).forEach((key) => {
    clearTimeout(timers[key])
  })
  Object.keys(timers).forEach((key) => {
    delete timers[key]
  })
}
