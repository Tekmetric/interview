import { getScrollableParent } from './get-scrollable-parent'

describe('getScrollableParent', () => {
  it('should return null if the node is null', () => {
    expect(getScrollableParent(null)).toBeNull()
  })

  it('should return the node itself if it has overflowX auto', () => {
    const node = document.createElement('div')

    Object.defineProperty(window, 'getComputedStyle', {
      value: () => ({ overflowX: 'auto' })
    })

    expect(getScrollableParent(node)).toBe(node)
  })

  it('should return the node itself if it has overflowX scroll', () => {
    const node = document.createElement('div')

    Object.defineProperty(window, 'getComputedStyle', {
      value: () => ({ overflowX: 'scroll' })
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
          ? { overflowX: 'auto' }
          : { overflowX: 'visible' }
      }
    })

    expect(getScrollableParent(child)).toBe(parent)
  })

  it('should return null if no scrollable parent is found', () => {
    const node = document.createElement('div')

    Object.defineProperty(window, 'getComputedStyle', {
      value: () => ({ overflowX: 'visible' })
    })

    expect(getScrollableParent(node)).toBeNull()
  })
})
