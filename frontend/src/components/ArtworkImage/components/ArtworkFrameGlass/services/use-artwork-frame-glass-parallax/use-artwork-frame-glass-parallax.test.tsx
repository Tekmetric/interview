import { act, render, renderHook } from '@testing-library/react'
import React from 'react'

import { getScrollableParent } from '../get-scrollable-parent/get-scrollable-parent'
import { useOnScreen } from '../use-on-screen/use-on-screen'
import { useArtworkFrameGlassParallax } from './use-artwork-frame-glass-parallax'

jest.mock('../use-on-screen/use-on-screen', () => ({
  useOnScreen: jest.fn()
}))

jest.mock('../get-scrollable-parent/get-scrollable-parent', () => ({
  getScrollableParent: jest.fn()
}))

const useOnScreenMock = useOnScreen as jest.Mock
const getScrollableParentMock = getScrollableParent as jest.Mock

describe('useArtworkFrameGlassParallax', () => {
  const scrollableParentMock = document.createElement('div')

  beforeEach(() => {
    useOnScreenMock.mockReturnValue({
      isIntersecting: true
    })

    getScrollableParentMock.mockReturnValue(scrollableParentMock)
  })

  it('should initialize horizontalOffset', () => {
    const { result } = renderHook(() => useArtworkFrameGlassParallax())

    expect(result.current.horizontalOffset).toBe(0)
  })

  it('should set horizontalOffset correctly when scrolled', () => {
    Object.defineProperty(window, 'innerWidth', { value: 800, writable: true })

    jest
      .spyOn(window, 'requestAnimationFrame')
      .mockImplementation((callback: FrameRequestCallback) => {
        callback(0)
        return 0
      })

    const TestComponent = () => {
      const { glassRef, horizontalOffset } = useArtworkFrameGlassParallax()
      return (
        <div>
          <div ref={glassRef} data-testid='glass-div'></div>
          <div data-testid='offset-value'>{horizontalOffset}</div>
        </div>
      )
    }

    const { getByTestId } = render(<TestComponent />)

    const glassDiv = getByTestId('glass-div') as HTMLDivElement

    glassDiv.getBoundingClientRect = jest.fn(
      () =>
        ({
          left: 100,
          width: 200
        }) as DOMRect
    )

    act(() => {
      const scrollEvent = new Event('scroll')
      scrollableParentMock.dispatchEvent(scrollEvent)
    })

    const parallaxIntensity = 0.2
    const viewportCenter = window.innerWidth / 2
    const glassCenter = 100 + 200 / 2
    const frameOffset = (glassCenter - viewportCenter) * parallaxIntensity

    const offsetValue = getByTestId('offset-value').textContent
    expect(Number(offsetValue)).toBe(frameOffset)
  })

  it('should not set horizontalOffset when not intersecting', () => {
    useOnScreenMock.mockReturnValue({ isIntersecting: false })

    const TestComponent = () => {
      const { glassRef, horizontalOffset } = useArtworkFrameGlassParallax()
      return (
        <div>
          <div ref={glassRef} data-testid='glass-div'></div>
          <div data-testid='offset-value'>{horizontalOffset}</div>
        </div>
      )
    }

    const { getByTestId } = render(<TestComponent />)

    const glassDiv = getByTestId('glass-div') as HTMLDivElement

    glassDiv.getBoundingClientRect = jest.fn(
      () =>
        ({
          left: 100,
          width: 200
        }) as DOMRect
    )

    act(() => {
      const scrollEvent = new Event('scroll')
      window.dispatchEvent(scrollEvent)
    })

    const offsetValue = getByTestId('offset-value').textContent

    expect(offsetValue).toBe('0')
  })
})
