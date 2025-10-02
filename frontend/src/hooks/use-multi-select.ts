import { useContext, useEffect } from 'react'
import { useSearch } from 'wouter'
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
 * - Cmd/Ctrl+A: Select all (disabled when sidebar is open)
 * - Escape: Clear selection
 */
export function useMultiSelectKeyboard(orderIds: string[]) {
  const { selectAll, clearSelection } = useMultiSelect()
  const search = useSearch()

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      const searchParams = new URLSearchParams(search)
      const isSidebarOpen = searchParams.has('roId') || searchParams.has('createRO')

      // Cmd/Ctrl+A: Select all (skip if sidebar is open)
      if ((e.metaKey || e.ctrlKey) && e.key === 'a') {
        if (!isSidebarOpen) {
          e.preventDefault()
          selectAll(orderIds)
        }
      }

      // Escape: Clear selection
      if (e.key === 'Escape') {
        clearSelection()
      }
    }

    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [orderIds, selectAll, clearSelection, search])
}
