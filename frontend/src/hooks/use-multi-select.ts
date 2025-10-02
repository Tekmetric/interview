import { useContext, useEffect } from 'react'
import { SelectionContext } from '@/contexts/selection-context'

export function useMultiSelect() {
  const context = useContext(SelectionContext)
  if (!context) {
    throw new Error('useMultiSelect must be used within SelectionProvider')
  }
  return context
}

/**
 * Hook to enable keyboard shortcuts for multi-select
 * - Cmd/Ctrl+A: Select all
 * - Escape: Clear selection
 */
export function useMultiSelectKeyboard(orderIds: string[]) {
  const { selectAll, clearSelection } = useMultiSelect()

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // Cmd/Ctrl+A: Select all
      if ((e.metaKey || e.ctrlKey) && e.key === 'a') {
        e.preventDefault()
        selectAll(orderIds)
      }

      // Escape: Clear selection
      if (e.key === 'Escape') {
        clearSelection()
      }
    }

    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [orderIds, selectAll, clearSelection])
}
