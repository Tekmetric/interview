import { getScrollableParent } from './get-scrollable-parent'

describe('getScrollableParent', () => {
  it('should return null if the node is null', () => {
    expect(getScrollableParent(null)).toBeNull()
  })

  it('should return the node itself if it has overflowY auto', () => {
    const node = document.createElement('div')

    Object.defineProperty(window, 'getComputedStyle', {
      value: () => ({ overflowY: 'auto' })
    })

    expect(getScrollableParent(node)).toBe(node)
  })

  it('should return the node itself if it has overflowY scroll', () => {
    const node = document.createElement('div')

    Object.defineProperty(window, 'getComputedStyle', {
      value: () => ({ overflowY: 'scroll' })
    })

    expect(getScrollableParent(node)).toBe(node)
  })

  it('should return the scrollable parent node', () => {
    const parent = document.createElement('div')
    const child = document.createElement('div')

    parent.appendChild(child)

    Object.defineProperty(window, 'getComputedStyle', {
      value: (node: HTMLElement) => {
        return node === parent
          ? { overflowY: 'auto' }
          : { overflowY: 'visible' }
      }
    })

    expect(getScrollableParent(child)).toBe(parent)
  })

  it('should return null if no scrollable parent is found', () => {
    const node = document.createElement('div')

    Object.defineProperty(window, 'getComputedStyle', {
      value: () => ({ overflowY: 'visible' })
    })

    expect(getScrollableParent(node)).toBeNull()
  })
})
