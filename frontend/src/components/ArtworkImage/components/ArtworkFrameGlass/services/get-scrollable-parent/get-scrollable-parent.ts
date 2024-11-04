export const getScrollableParent = (
  node: HTMLElement | null
): HTMLElement | null => {
  if (!node) {
    return null
  }

  const overflowX = window.getComputedStyle(node).overflowX

  if (overflowX === 'auto' || overflowX === 'scroll') {
    return node
  }

  return getScrollableParent(node.parentElement)
}
