import { describe, it, expect, beforeEach, vi } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { usePersistedState } from '../use-persisted-state'
import * as storage from '@/lib/storage'

vi.mock('@/lib/storage', () => ({
  getItem: vi.fn(),
  setItem: vi.fn(),
}))

describe('usePersistedState', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(storage.getItem).mockImplementation((_, defaultValue) => defaultValue)
  })

  it('should initialize with default value', () => {
    const { result } = renderHook(() => usePersistedState('test-key', 'default'))

    expect(result.current[0]).toBe('default')
    expect(storage.getItem).toHaveBeenCalledWith('test-key', 'default')
  })

  it('should initialize with persisted value from storage', () => {
    vi.mocked(storage.getItem).mockReturnValue('persisted-value')

    const { result } = renderHook(() => usePersistedState('test-key', 'default'))

    expect(result.current[0]).toBe('persisted-value')
    expect(storage.getItem).toHaveBeenCalledWith('test-key', 'default')
  })

  it('should update state and persist to storage', () => {
    const { result } = renderHook(() => usePersistedState('test-key', 'initial'))

    act(() => {
      result.current[1]('updated')
    })

    expect(result.current[0]).toBe('updated')
    expect(storage.setItem).toHaveBeenCalledWith('test-key', 'updated', true)
  })

  it('should support functional updates', () => {
    const { result } = renderHook(() => usePersistedState('test-key', 5))

    act(() => {
      result.current[1]((prev) => prev + 1)
    })

    expect(result.current[0]).toBe(6)
    expect(storage.setItem).toHaveBeenCalledWith('test-key', 6, true)
  })

  it('should not persist on initial mount', () => {
    renderHook(() => usePersistedState('test-key', 'initial'))

    // setItem should not be called on initial mount
    expect(storage.setItem).not.toHaveBeenCalled()
  })

  it('should persist on subsequent updates', () => {
    const { result } = renderHook(() => usePersistedState('test-key', 'initial'))

    expect(storage.setItem).not.toHaveBeenCalled()

    act(() => {
      result.current[1]('updated')
    })

    expect(storage.setItem).toHaveBeenCalledWith('test-key', 'updated', true)
  })

  it('should handle complex objects', () => {
    const initialObj = { name: 'John', age: 30 }
    const updatedObj = { name: 'Jane', age: 25 }

    const { result } = renderHook(() => usePersistedState('test-key', initialObj))

    act(() => {
      result.current[1](updatedObj)
    })

    expect(result.current[0]).toEqual(updatedObj)
    expect(storage.setItem).toHaveBeenCalledWith('test-key', updatedObj, true)
  })

  it('should handle arrays', () => {
    const { result } = renderHook(() => usePersistedState('test-key', [1, 2, 3]))

    act(() => {
      result.current[1]([4, 5, 6])
    })

    expect(result.current[0]).toEqual([4, 5, 6])
    expect(storage.setItem).toHaveBeenCalledWith('test-key', [4, 5, 6], true)
  })

  it('should handle boolean values', () => {
    const { result } = renderHook(() => usePersistedState('test-key', false))

    act(() => {
      result.current[1](true)
    })

    expect(result.current[0]).toBe(true)
    expect(storage.setItem).toHaveBeenCalledWith('test-key', true, true)
  })

  it('should handle null values', () => {
    const { result } = renderHook(() =>
      usePersistedState<string | null>('test-key', null),
    )

    act(() => {
      result.current[1]('value')
    })

    expect(result.current[0]).toBe('value')

    act(() => {
      result.current[1](null)
    })

    expect(result.current[0]).toBe(null)
    expect(storage.setItem).toHaveBeenCalledWith('test-key', null, true)
  })

  it('should handle multiple updates in succession', () => {
    const { result } = renderHook(() => usePersistedState('test-key', 0))

    act(() => {
      result.current[1](1)
      result.current[1](2)
      result.current[1](3)
    })

    expect(result.current[0]).toBe(3)
    expect(storage.setItem).toHaveBeenLastCalledWith('test-key', 3, true)
  })

  it('should use correct key for different instances', () => {
    const { result: result1 } = renderHook(() => usePersistedState('key1', 'value1'))
    const { result: result2 } = renderHook(() => usePersistedState('key2', 'value2'))

    expect(storage.getItem).toHaveBeenCalledWith('key1', 'value1')
    expect(storage.getItem).toHaveBeenCalledWith('key2', 'value2')

    act(() => {
      result1.current[1]('updated1')
    })

    act(() => {
      result2.current[1]('updated2')
    })

    expect(storage.setItem).toHaveBeenCalledWith('key1', 'updated1', true)
    expect(storage.setItem).toHaveBeenCalledWith('key2', 'updated2', true)
  })
})
