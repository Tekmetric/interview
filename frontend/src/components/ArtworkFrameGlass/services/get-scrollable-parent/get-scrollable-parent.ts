export const getScrollableParent = (
  node: HTMLElement | null
): HTMLElement | null => {
  if (!node) {
    return null
  }

  const overflowY = window.getComputedStyle(node).overflowY

  if (overflowY === 'auto' || overflowY === 'scroll') {
    return node
  }

  return getScrollableParent(node.parentElement)
}
