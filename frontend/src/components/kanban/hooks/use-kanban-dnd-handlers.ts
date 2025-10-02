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
        const overCenterY = over.rect.top + over.rect.height / 2
        const activeInitial = active.rect.current?.initial
        if (!activeInitial) {
          setDropIndicatorById({ [overId]: 'bottom' })
        } else {
          const activeCenterY =
            activeInitial.top + activeInitial.height / 2 + event.delta.y
          const position: 'top' | 'bottom' =
            activeCenterY < overCenterY ? 'top' : 'bottom'
          setDropIndicatorById({ [overId]: position })
        }
      } else {
        setDropIndicatorById({})
      }
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
      setDropIndicatorById({})

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

      // If moving across statuses, validate before applying
      if (targetStatus && targetStatus !== originalStatus) {
        const validation = canTransition(originalStatus, targetStatus, order)
        if (!validation.allowed) {
          toast.error(KANBAN_LABELS.CANNOT_MOVE, {
            description: validation.reason || KANBAN_LABELS.TRANSITION_NOT_ALLOWED,
            duration: 3000,
          })
          return
        }
      }

      // Local reorder so the card stays where it's dropped
      const next = [...localOrders]
      const fromIndex = next.findIndex((o) => o.id === orderId)
      if (fromIndex === -1) return
      const [moved] = next.splice(fromIndex, 1)

      if (targetStatus) {
        moved.status = targetStatus
      }

      let insertIndex = fromIndex
      if (overCard) {
        const overIndex = next.findIndex((o) => o.id === overCard.id)
        const pos = dropIndicatorById[overCard.id] || 'bottom'
        insertIndex =
          overIndex === -1 ? next.length : overIndex + (pos === 'bottom' ? 1 : 0)
      } else if (overStatus && targetStatus) {
        // Insert at end of the target status group
        let lastIndex = -1
        for (let i = 0; i < next.length; i += 1) {
          if (next[i].status === targetStatus) lastIndex = i
        }
        insertIndex = lastIndex === -1 ? next.length : lastIndex + 1
      }

      next.splice(insertIndex, 0, moved)
      setLocalOrders(next)

      // Persist status change if applicable
      if (targetStatus && targetStatus !== originalStatus) {
        onStatusChange(orderId, targetStatus)
      }
    },
    [localOrders, dropIndicatorById, onStatusChange],
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
