import { act, renderHook } from '@testing-library/react'
import { MutableRefObject } from 'react'

import { useOnScrollToEnd } from './use-on-scroll-to-end'

describe('useOnScrollToEnd', () => {
  const debounceIntervalMs = 100

  beforeEach(() => {
    jest.useFakeTimers()
  })

  afterEach(() => {
    jest.useRealTimers()
    jest.clearAllMocks()
  })

  it('should call onScrollToEnd when scrolled to end', () => {
    const onScrollToEnd = jest.fn()

    const { result } = renderHook(() =>
      useOnScrollToEnd<HTMLDivElement>({ onScrollToEnd })
    )

    const mockElement = {
      clientWidth: 500,
      scrollLeft: 500,
      scrollWidth: 1000
    } as unknown as HTMLDivElement

    act(() => {
      ;(
        result.current.containerRef as MutableRefObject<HTMLDivElement>
      ).current = mockElement
    })

    act(() => {
      result.current.handleScroll()
    })

    act(() => {
      jest.advanceTimersByTime(debounceIntervalMs)
    })

    expect(onScrollToEnd).toHaveBeenCalledTimes(1)
  })

  it('should not call onScrollToEnd when not scrolled to end', () => {
    const onScrollToEnd = jest.fn()

    const { result } = renderHook(() =>
      useOnScrollToEnd<HTMLDivElement>({ onScrollToEnd })
    )

    const mockElement = {
      clientWidth: 500,
      scrollLeft: 200,
      scrollWidth: 1000
    } as unknown as HTMLDivElement

    act(() => {
      ;(
        result.current.containerRef as MutableRefObject<HTMLDivElement>
      ).current = mockElement
    })

    act(() => {
      result.current.handleScroll()
    })

    act(() => {
      jest.advanceTimersByTime(debounceIntervalMs)
    })

    expect(onScrollToEnd).not.toHaveBeenCalled()
  })

  it('should handle multiple scroll events with debounce', () => {
    const onScrollToEnd = jest.fn()

    const { result } = renderHook(() =>
      useOnScrollToEnd<HTMLDivElement>({ onScrollToEnd })
    )

    const mockElement = {
      clientWidth: 500,
      scrollLeft: 200,
      scrollWidth: 1000
    } as unknown as HTMLDivElement

    act(() => {
      ;(
        result.current.containerRef as MutableRefObject<HTMLDivElement>
      ).current = mockElement
    })

    act(() => {
      result.current.handleScroll()
      result.current.handleScroll()
      result.current.handleScroll()
    })

    act(() => {
      jest.advanceTimersByTime(debounceIntervalMs - 10)
    })

    expect(onScrollToEnd).not.toHaveBeenCalled()

    mockElement.scrollLeft = 500

    act(() => {
      result.current.handleScroll()
    })

    act(() => {
      jest.advanceTimersByTime(10)
      jest.advanceTimersByTime(debounceIntervalMs)
    })

    expect(onScrollToEnd).toHaveBeenCalledTimes(1)
  })
})
