import { createContext, ReactNode, useCallback, useMemo, useState } from 'react'
import type { SelectionState, SelectionContextValue } from '@/types/selection'

const DEFAULT_SELECTION: SelectionState = {
  selectedIds: new Set(),
  isSelecting: false,
  lastSelectedId: null,
}

export const SelectionContext = createContext<SelectionContextValue | null>(null)

export function SelectionProvider({ children }: { children: ReactNode }) {
  const [selection, setSelection] = useState<SelectionState>(DEFAULT_SELECTION)

  const toggleSelection = useCallback((orderId: string) => {
    setSelection((prev) => {
      const newSelectedIds = new Set(prev.selectedIds)
      if (newSelectedIds.has(orderId)) {
        newSelectedIds.delete(orderId)
      } else {
        newSelectedIds.add(orderId)
      }
      return {
        selectedIds: newSelectedIds,
        isSelecting: newSelectedIds.size > 0,
        lastSelectedId: orderId,
      }
    })
  }, [])

  const selectRange = useCallback((orderId: string) => {
    setSelection((prev) => {
      if (!prev.lastSelectedId) {
        // No previous selection, just select this one
        return {
          selectedIds: new Set([orderId]),
          isSelecting: true,
          lastSelectedId: orderId,
        }
      }

      // This will be enhanced when we have order positions available
      // For now, just add to selection
      const newSelectedIds = new Set(prev.selectedIds)
      newSelectedIds.add(orderId)
      return {
        selectedIds: newSelectedIds,
        isSelecting: true,
        lastSelectedId: orderId,
      }
    })
  }, [])

  const selectAll = useCallback((orderIds: string[]) => {
    setSelection({
      selectedIds: new Set(orderIds),
      isSelecting: orderIds.length > 0,
      lastSelectedId: orderIds[orderIds.length - 1] || null,
    })
  }, [])

  const clearSelection = useCallback(() => {
    setSelection(DEFAULT_SELECTION)
  }, [])

  const isSelected = useCallback(
    (orderId: string) => {
      return selection.selectedIds.has(orderId)
    },
    [selection.selectedIds],
  )

  const value = useMemo(
    () => ({
      selection,
      toggleSelection,
      selectRange,
      selectAll,
      clearSelection,
      isSelected,
    }),
    [selection, toggleSelection, selectRange, selectAll, clearSelection, isSelected],
  )

  return <SelectionContext.Provider value={value}>{children}</SelectionContext.Provider>
}
