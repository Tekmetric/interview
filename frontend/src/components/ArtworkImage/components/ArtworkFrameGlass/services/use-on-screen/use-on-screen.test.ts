import { act, renderHook } from '@testing-library/react'
import { MutableRefObject } from 'react'

import { useOnScreen } from './use-on-screen'

describe('useOnScreen', () => {
  let observeMock: jest.Mock
  let unobserveMock: jest.Mock
  let disconnectMock: jest.Mock

  beforeEach(() => {
    observeMock = jest.fn()
    unobserveMock = jest.fn()
    disconnectMock = jest.fn()

    class IntersectionObserverMock {
      constructor(public callback: IntersectionObserverCallback) {}
      observe = observeMock
      unobserve = unobserveMock
      disconnect = disconnectMock
    }

    ;(global as any).IntersectionObserver = IntersectionObserverMock
  })

  afterEach(() => {
    jest.clearAllMocks()
  })

  it('should initialize with isIntersecting as false', () => {
    const ref = { current: null } as MutableRefObject<HTMLDivElement | null>
    const { result } = renderHook(() => useOnScreen(ref))

    expect(result.current.isIntersecting).toBe(false)
  })

  it('should update isIntersecting when the observer callback is triggered', () => {
    let observerCallback: IntersectionObserverCallback = () => {}

    ;(global as any).IntersectionObserver = class {
      constructor(callback: IntersectionObserverCallback) {
        observerCallback = callback
      }
      observe = observeMock
      unobserve = unobserveMock
      disconnect = disconnectMock
    }

    const ref = {
      current: document.createElement('div')
    } as MutableRefObject<HTMLDivElement>
    const { result } = renderHook(() => useOnScreen(ref))

    expect(result.current.isIntersecting).toBe(false)

    act(() => {
      observerCallback(
        [
          {
            isIntersecting: true
          } as IntersectionObserverEntry
        ],
        {} as IntersectionObserver
      )
    })

    expect(result.current.isIntersecting).toBe(true)

    act(() => {
      observerCallback(
        [
          {
            isIntersecting: false
          } as IntersectionObserverEntry
        ],
        {} as IntersectionObserver
      )
    })

    expect(result.current.isIntersecting).toBe(false)
  })

  it('should unobserve and disconnect on cleanup', () => {
    const ref = {
      current: document.createElement('div')
    } as MutableRefObject<HTMLDivElement>
    const { unmount } = renderHook(() => useOnScreen(ref))

    expect(observeMock).toHaveBeenCalledWith(ref.current)

    unmount()

    expect(unobserveMock).toHaveBeenCalledWith(ref.current)
    expect(disconnectMock).toHaveBeenCalled()
  })
})
