import { render, fireEvent } from '@testing-library/react'
import SmoothScroll from '../SmoothScroll'

describe('SmoothScroll', () => {
  let originalScrollTo: typeof window.scrollTo

  beforeEach(() => {
    originalScrollTo = window.scrollTo
    window.scrollTo = jest.fn()
  })

  afterEach(() => {
    window.scrollTo = originalScrollTo
    document.body.innerHTML = ''
    jest.clearAllMocks()
  })

  it('smooth scrolls to target element on anchor click', () => {
    const header = document.createElement('header')
    header.style.height = '100px'
    header.getBoundingClientRect = jest.fn(() => ({ height: 100 })) as any
    document.body.appendChild(header)

    const mobileMenu = document.createElement('div')
    mobileMenu.className = 'mobile-menu'
    mobileMenu.style.height = '50px'
    mobileMenu.getBoundingClientRect = jest.fn(() => ({ height: 50 })) as any
    document.body.appendChild(mobileMenu)

    const targetElement = document.createElement('div')
    targetElement.id = 'test-target'
    targetElement.getBoundingClientRect = jest.fn(() => ({ top: 200 })) as any
    document.body.appendChild(targetElement)

    render(<SmoothScroll />)

    const anchor = document.createElement('a')
    anchor.href = '#test-target'
    document.body.appendChild(anchor)

    fireEvent.click(anchor)

    expect(window.scrollTo).toHaveBeenCalledWith({
      top: 50,
      behavior: 'smooth',
    })
  })

  it('does nothing if the clicked element has no hash', () => {
    render(<SmoothScroll />)
    const anchor = document.createElement('a')
    anchor.href = 'https://example.com'
    document.body.appendChild(anchor)

    fireEvent.click(anchor)

    expect(window.scrollTo).not.toHaveBeenCalled()
  })
})
