import { act, render } from '@testing-library/react'
import React from 'react'

import { getArtworkFrameGlassParallaxOffset } from '../get-artwork-frame-glass-parallax-offset/get-artwork-frame-glass-parallax-offset'
import { getScrollableParent } from '../get-scrollable-parent/get-scrollable-parent'
import { useOnScreen } from '../use-on-screen/use-on-screen'
import { useArtworkFrameGlassParallax } from './use-artwork-frame-glass-parallax'

jest.mock('../use-on-screen/use-on-screen')
jest.mock('../get-scrollable-parent/get-scrollable-parent')
jest.mock(
  '../get-artwork-frame-glass-parallax-offset/get-artwork-frame-glass-parallax-offset'
)

describe('useArtworkFrameGlassParallax', () => {
  const useOnScreenMock = useOnScreen as jest.Mock
  const getScrollableParentMock = getScrollableParent as jest.Mock
  const getParallaxOffsetMock = getArtworkFrameGlassParallaxOffset as jest.Mock

  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('should call getArtworkFrameGlassParallaxOffset when isIntersecting is true', async () => {
    useOnScreenMock.mockReturnValue({ isIntersecting: true })

    const mockScrollableParent = document.createElement('div')
    getScrollableParentMock.mockReturnValue(mockScrollableParent)

    const addEventListenerSpy = jest.spyOn(
      mockScrollableParent,
      'addEventListener'
    )

    const TestComponent = () => {
      const { glassRef } = useArtworkFrameGlassParallax()
      return <div ref={glassRef}></div>
    }

    await act(async () => {
      render(<TestComponent />)
    })

    expect(addEventListenerSpy).toHaveBeenCalledWith(
      'scroll',
      expect.any(Function)
    )
    const scrollHandler = addEventListenerSpy.mock.calls[0][1] as EventListener

    const rafSpy = jest
      .spyOn(window, 'requestAnimationFrame')
      .mockImplementation(callback => {
        callback(0)
        return 0
      })

    act(() => {
      scrollHandler(new Event('scroll'))
    })

    expect(getParallaxOffsetMock).toHaveBeenCalled()

    rafSpy.mockRestore()
  })

  it('should not call getArtworkFrameGlassParallaxOffset when isIntersecting is false', async () => {
    useOnScreenMock.mockReturnValue({ isIntersecting: false })

    const mockScrollableParent = document.createElement('div')
    getScrollableParentMock.mockReturnValue(mockScrollableParent)

    const addEventListenerSpy = jest.spyOn(
      mockScrollableParent,
      'addEventListener'
    )

    const TestComponent = () => {
      const { glassRef } = useArtworkFrameGlassParallax()
      return <div ref={glassRef}></div>
    }

    await act(async () => {
      render(<TestComponent />)
    })

    expect(addEventListenerSpy).toHaveBeenCalledWith(
      'scroll',
      expect.any(Function)
    )

    const scrollHandler = addEventListenerSpy.mock.calls[0][1] as EventListener

    const rafSpy = jest
      .spyOn(window, 'requestAnimationFrame')
      .mockImplementation(callback => {
        callback(0)
        return 0
      })

    act(() => {
      scrollHandler(new Event('scroll'))
    })

    expect(getParallaxOffsetMock).not.toHaveBeenCalled()

    // Clean up
    rafSpy.mockRestore()
  })
})
