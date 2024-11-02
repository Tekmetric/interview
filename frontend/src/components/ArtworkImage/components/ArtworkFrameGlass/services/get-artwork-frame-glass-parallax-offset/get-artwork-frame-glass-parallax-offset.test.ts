import { getArtworkFrameGlassParallaxOffset } from './get-artwork-frame-glass-parallax-offset'

describe('getArtworkFrameGlassParallaxOffset', () => {
  const originalInnerWidth = window.innerWidth

  beforeEach(() => {
    Object.defineProperty(window, 'innerWidth', {
      writable: true,
      configurable: true,
      value: originalInnerWidth
    })
  })

  afterEach(() => {
    Object.defineProperty(window, 'innerWidth', {
      writable: true,
      configurable: true,
      value: originalInnerWidth
    })
  })

  it('should calculate the correct parallax offset when glass is centered', () => {
    Object.defineProperty(window, 'innerWidth', { value: 800, writable: true })

    const glassRect = {
      left: 300,
      width: 200,
      right: 500
    } as DOMRect

    const result = getArtworkFrameGlassParallaxOffset(glassRect)

    expect(result).toBe(0)
  })

  it('should calculate a positive parallax offset when glass is to the right of center', () => {
    Object.defineProperty(window, 'innerWidth', { value: 800, writable: true })

    const glassRect = {
      left: 500,
      width: 200,
      right: 700
    } as DOMRect

    const result = getArtworkFrameGlassParallaxOffset(glassRect)

    const viewportCenter = 400 // 800 / 2
    const glassCenter = 500 + 100 // left + width / 2

    const expectedOffset = (glassCenter - viewportCenter) * 0.2

    expect(result).toBe(expectedOffset)
  })

  it('should calculate a negative parallax offset when glass is to the left of center', () => {
    Object.defineProperty(window, 'innerWidth', { value: 800, writable: true })

    const glassRect = {
      left: 100,
      width: 200,
      right: 300
    } as DOMRect

    const result = getArtworkFrameGlassParallaxOffset(glassRect)

    const viewportCenter = 400 // 800 / 2
    const glassCenter = 100 + 100 // left + width / 2

    const expectedOffset = (glassCenter - viewportCenter) * 0.2

    expect(result).toBe(expectedOffset)
  })

  it('should handle different viewport widths', () => {
    Object.defineProperty(window, 'innerWidth', { value: 1200, writable: true })

    const glassRect = {
      left: 400,
      width: 400,
      right: 800
    } as DOMRect

    const result = getArtworkFrameGlassParallaxOffset(glassRect)

    const viewportCenter = 600 // 1200 / 2
    const glassCenter = 400 + 200 // left + width / 2

    const expectedOffset = (glassCenter - viewportCenter) * 0.2

    expect(result).toBe(expectedOffset)
  })

  it('should handle zero width elements', () => {
    Object.defineProperty(window, 'innerWidth', { value: 800, writable: true })

    const glassRect = {
      left: 400,
      width: 0,
      right: 400
    } as DOMRect

    const result = getArtworkFrameGlassParallaxOffset(glassRect)

    const viewportCenter = 400 // 800 / 2
    const glassCenter = 400 + 0 // left + width / 2

    const expectedOffset = (glassCenter - viewportCenter) * 0.2

    expect(result).toBe(0) // Since glassCenter equals viewportCenter

    expect(result).toBe(expectedOffset)
  })

  it('should handle elements with negative left positions', () => {
    Object.defineProperty(window, 'innerWidth', { value: 800, writable: true })

    const glassRect = {
      left: -200,
      width: 200,
      right: 0,
      x: -200,
      toJSON: () => {}
    } as DOMRect

    const result = getArtworkFrameGlassParallaxOffset(glassRect)

    const viewportCenter = 400 // 800 / 2
    const glassCenter = -200 + 100 // left + width / 2

    const expectedOffset = (glassCenter - viewportCenter) * 0.2

    expect(result).toBe(expectedOffset)
  })

  it('should handle elements larger than viewport', () => {
    Object.defineProperty(window, 'innerWidth', { value: 800, writable: true })

    const glassRect = {
      left: -100,
      width: 1000,
      right: 900,
      x: -100,
      toJSON: () => {}
    } as DOMRect

    const result = getArtworkFrameGlassParallaxOffset(glassRect)

    const viewportCenter = 400 // 800 / 2
    const glassCenter = -100 + 500 // left + width / 2

    const expectedOffset = (glassCenter - viewportCenter) * 0.2

    expect(result).toBe(expectedOffset)
  })
})
