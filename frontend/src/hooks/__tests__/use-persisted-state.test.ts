
import { renderHook, act } from '@testing-library/react'
import { usePersistedState } from '../use-persisted-state'
import * as storage from '@/lib/storage'

// Mock the storage module
vi.mock('@/lib/storage', () => ({
  getItem: vi.fn(),
  setItem: vi.fn(),
}))

const mockedGetItem = vi.mocked(storage.getItem)
const mockedSetItem = vi.mocked(storage.setItem)

describe('usePersistedState', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should return the default value initially when storage is empty', () => {
    mockedGetItem.mockReturnValue(null)
    const { result } = renderHook(() => usePersistedState('test-key', 'default'))

    expect(result.current[0]).toBe('default')
  })

  it('should return the stored value initially when storage has a value', () => {
    mockedGetItem.mockReturnValue('stored-value')
    const { result } = renderHook(() => usePersistedState('test-key', 'default'))

    expect(result.current[0]).toBe('stored-value')
  })

  it('should update the state when the setter is called', () => {
    mockedGetItem.mockReturnValue('initial')
    const { result } = renderHook(() => usePersistedState('test-key', 'initial'))

    act(() => {
      result.current[1]('new-value')
    })

    expect(result.current[0]).toBe('new-value')
  })

  it('should persist the updated state to storage', () => {
    mockedGetItem.mockReturnValue('initial')
    const { result } = renderHook(() => usePersistedState('test-key', 'initial'))

    act(() => {
      result.current[1]('new-value')
    })

    // First render is skipped, so it should be called once on update
    expect(mockedSetItem).toHaveBeenCalledTimes(1)
    expect(mockedSetItem).toHaveBeenCalledWith('test-key', 'new-value', true)
  })

  it('should not write to storage on initial render', () => {
    renderHook(() => usePersistedState('test-key', 'default'))
    expect(mockedSetItem).not.toHaveBeenCalled()
  })

  it('should handle functional updates', () => {
    mockedGetItem.mockReturnValue(10)
    const { result } = renderHook(() => usePersistedState('count', 10))

    act(() => {
      result.current[1]((prev) => prev + 1)
    })

    expect(result.current[0]).toBe(11)
    expect(mockedSetItem).toHaveBeenCalledWith('count', 11, true)
  })
})
