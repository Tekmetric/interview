export interface SelectionState {
  selectedIds: Set<string>
  isSelecting: boolean
  lastSelectedId: string | null
}

export interface SelectionContextValue {
  selection: SelectionState
  toggleSelection: (orderId: string) => void
  selectRange: (orderId: string) => void
  selectAll: (orderIds: string[]) => void
  clearSelection: () => void
  isSelected: (orderId: string) => boolean
}
