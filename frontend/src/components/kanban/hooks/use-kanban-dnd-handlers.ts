import { useState, useCallback, useEffect } from 'react'
import type { DragStartEvent, DragOverEvent, DragEndEvent } from '@dnd-kit/core'
import { toast } from 'sonner'
import { canTransition } from '@shared/transitions'
import { KANBAN_LABELS, KANBAN_COLUMNS } from '@shared/constants'
import type { RepairOrder, RepairOrderStatus } from '@shared/types'

type UseKanbanDndHandlersProps = {
  orders: RepairOrder[]
  onStatusChange: (orderId: string, newStatus: RepairOrderStatus) => void
  clearSelection: () => void
}

type UseKanbanDndHandlersReturn = {
  activeId: string | null
  dragOverStatus: RepairOrderStatus | null
  isValidDrop: boolean
  validationMessage: string
  localOrders: RepairOrder[]
  dropIndicatorById: Record<string, 'top' | 'bottom'>
  activeOrder: RepairOrder | null
  handleDragStart: (event: DragStartEvent) => void
  handleDragOver: (event: DragOverEvent) => void
  handleDragEnd: (event: DragEndEvent) => void
}

/**
 * Business logic for Kanban drag-and-drop operations.
 * Handles validation, visual feedback, local reordering, and status updates.
 */
export function useKanbanDndHandlers({
  orders,
  onStatusChange,
  clearSelection,
}: UseKanbanDndHandlersProps): UseKanbanDndHandlersReturn {
  const [activeId, setActiveId] = useState<string | null>(null)
  const [dragOverStatus, setDragOverStatus] = useState<RepairOrderStatus | null>(null)
  const [isValidDrop, setIsValidDrop] = useState<boolean>(true)
  const [validationMessage, setValidationMessage] = useState<string>('')
  const [localOrders, setLocalOrders] = useState<RepairOrder[]>(orders)
  const [dropIndicatorById, setDropIndicatorById] = useState<
    Record<string, 'top' | 'bottom'>
  >({})
  const [lastOverCardId, setLastOverCardId] = useState<string | null>(null)

  // Sync local orders when prop changes
  useEffect(() => {
    setLocalOrders(orders)
  }, [orders])

  const activeOrder = localOrders.find((o) => o.id === activeId) ?? null

  const handleDragStart = useCallback(
    (event: DragStartEvent) => {
      setActiveId(event.active.id as string)
      clearSelection()
    },
    [clearSelection],
  )

  const handleDragOver = useCallback(
    (event: DragOverEvent) => {
      const { active, over } = event

      if (!over) {
        setDragOverStatus(null)
        setIsValidDrop(true)
        setValidationMessage('')
        setDropIndicatorById({})
        return
      }

      const activeId = active.id as string
      const overId = over.id as string

      const order = localOrders.find((o) => o.id === activeId)
      if (!order) return

      // Check if we're over a column (status ID) or a card
      const overStatus = KANBAN_COLUMNS.find((col) => col.status === overId)?.status
      const overCard = localOrders.find((o) => o.id === overId)

      let targetStatus: RepairOrderStatus | null = null

      if (overStatus) {
        targetStatus = overStatus
      } else if (overCard) {
        targetStatus = overCard.status
      }

      if (targetStatus) {
        setDragOverStatus(targetStatus)

        // Validate the transition (visual feedback only, no status change)
        const validation = canTransition(order.status, targetStatus, order)
        setIsValidDrop(validation.allowed)
        setValidationMessage(validation.reason || '')
      }

      // Compute drop indicator position (top/bottom) when hovering over a card
      if (overCard && over.rect) {
        setLastOverCardId(overId)
        const overCenterY = over.rect.top + over.rect.height / 2

        // Use translated rect for accurate position during drag (accounts for scroll/transform)
        const translated = active.rect.current?.translated
        const activeCenterY = translated
          ? translated.top + translated.height / 2
          : (active.rect.current?.initial?.top ?? 0) +
            (active.rect.current?.initial?.height ?? 0) / 2 +
            event.delta.y

        const position: 'top' | 'bottom' = activeCenterY < overCenterY ? 'top' : 'bottom'
        setDropIndicatorById({ [overId]: position })
      } else if (overStatus && !overCard && over.rect) {
        // Column fallback: only if we DON'T already have an anchor in this column
        const colOrders = localOrders.filter((o) => o.status === overStatus)
        const hasAnchorInColumn =
          lastOverCardId && colOrders.some((o) => o.id === lastOverCardId)

        if (!hasAnchorInColumn && colOrders.length && active.rect.current) {
          const translated = active.rect.current.translated
          const activeCenterY = translated
            ? translated.top + translated.height / 2
            : (active.rect.current.initial?.top ?? 0) +
              (active.rect.current.initial?.height ?? 0) / 2 +
              event.delta.y
          const colCenterY = over.rect.top + over.rect.height / 2

          // Choose first or last card as anchor based on position
          const anchorCard =
            activeCenterY <= colCenterY ? colOrders[0] : colOrders[colOrders.length - 1]
          const position: 'top' | 'bottom' = activeCenterY <= colCenterY ? 'top' : 'bottom'

          setLastOverCardId(anchorCard.id)
          setDropIndicatorById({ [anchorCard.id]: position })
        }
      } else if (!overStatus) {
        // Only clear indicators when not over anything meaningful
        setDropIndicatorById({})
      }
      // Preserve lastOverCardId and indicators when over becomes a column
    },
    [localOrders],
  )

  const handleDragEnd = useCallback(
    (event: DragEndEvent) => {
      const { active, over } = event

      setActiveId(null)
      setDragOverStatus(null)
      setIsValidDrop(true)
      setValidationMessage('')

      if (!over) {
        return
      }

      const orderId = active.id as string
      const overId = over.id as string

      const order = localOrders.find((o) => o.id === orderId)
      if (!order) return

      // Check if we're dropping on a column or a card
      const overStatus = KANBAN_COLUMNS.find((col) => col.status === overId)?.status
      const overCard = localOrders.find((o) => o.id === overId)

      let targetStatus: RepairOrderStatus | null = null

      if (overStatus) {
        targetStatus = overStatus
      } else if (overCard) {
        targetStatus = overCard.status
      }

      const originalStatus = order.status

      // Same-position no-op: dropping card onto itself
      if (
        targetStatus === originalStatus &&
        (overCard?.id === orderId || lastOverCardId === orderId)
      ) {
        setDropIndicatorById({})
        setLastOverCardId(null)
        return
      }

      // If moving across statuses, validate BEFORE modifying arrays
      if (targetStatus && targetStatus !== originalStatus) {
        const validation = canTransition(originalStatus, targetStatus, order)
        if (!validation.allowed) {
          toast.error(KANBAN_LABELS.CANNOT_MOVE, {
            description: validation.reason || KANBAN_LABELS.TRANSITION_NOT_ALLOWED,
            duration: 3000,
          })
          // Clear drop state before returning
          setDropIndicatorById({})
          setLastOverCardId(null)
          return
        }
      }

      // Validation passed (or same-column move): proceed with reordering
      const next = [...localOrders]
      const fromIndex = next.findIndex((o) => o.id === orderId)
      if (fromIndex === -1) return
      const [moved] = next.splice(fromIndex, 1)

      // Update status for cross-column moves
      if (targetStatus && targetStatus !== originalStatus) {
        moved.status = targetStatus
      }

      // Determine anchor card: prefer overCard, fallback to lastOverCardId
      // For cross-column, verify anchor belongs to target status
      const anchorCardId =
        overCard?.id ??
        (lastOverCardId &&
        next.some((o) => o.id === lastOverCardId && o.status === (targetStatus ?? originalStatus))
          ? lastOverCardId
          : null)

      let insertIndex = next.length
      if (anchorCardId) {
        const anchorIndex = next.findIndex((o) => o.id === anchorCardId)
        const pos = dropIndicatorById[anchorCardId] || 'bottom'
        insertIndex =
          anchorIndex === -1 ? next.length : anchorIndex + (pos === 'bottom' ? 1 : 0)
      }

      next.splice(insertIndex, 0, moved)
      setLocalOrders(next)

      // Clear drop state
      setDropIndicatorById({})
      setLastOverCardId(null)

      // Persist cross-column status change
      if (targetStatus && targetStatus !== originalStatus) {
        onStatusChange(orderId, targetStatus)
      }
    },
    [localOrders, dropIndicatorById, onStatusChange, lastOverCardId],
  )

  return {
    activeId,
    dragOverStatus,
    isValidDrop,
    validationMessage,
    localOrders,
    dropIndicatorById,
    activeOrder,
    handleDragStart,
    handleDragOver,
    handleDragEnd,
  }
}
