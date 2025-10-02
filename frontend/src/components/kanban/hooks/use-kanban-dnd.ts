import { useState, useMemo } from 'react'
import { DragEndEvent, DragOverEvent, DragStartEvent } from '@dnd-kit/core'
import { canTransition } from '@shared/transitions'
import { toast } from 'sonner'
import type { RepairOrder, RepairOrderStatus } from '@shared/types'
import { KANBAN_LABELS, RO_STATUS } from '@shared/constants'

export const COLUMNS: Array<{
  status: RepairOrderStatus
  title: string
  color: string
}> = [
  {
    status: RO_STATUS.NEW,
    title: KANBAN_LABELS.STATUS.NEW,
    color: 'bg-blue-100 text-blue-700',
  },
  {
    status: RO_STATUS.AWAITING_APPROVAL,
    title: KANBAN_LABELS.STATUS.AWAITING_APPROVAL,
    color: 'bg-amber-100 text-amber-700',
  },
  {
    status: RO_STATUS.IN_PROGRESS,
    title: KANBAN_LABELS.STATUS.IN_PROGRESS,
    color: 'bg-indigo-100 text-indigo-700',
  },
  {
    status: RO_STATUS.WAITING_PARTS,
    title: KANBAN_LABELS.STATUS.WAITING_PARTS,
    color: 'bg-orange-100 text-orange-700',
  },
  {
    status: RO_STATUS.COMPLETED,
    title: KANBAN_LABELS.STATUS.COMPLETED,
    color: 'bg-green-100 text-green-700',
  },
]

export function useKanbanDnd(
  orders: RepairOrder[],
  onStatusChange: (orderId: string, newStatus: RepairOrderStatus) => void,
) {
  const [activeId, setActiveId] = useState<string | null>(null)
  const [dragOverStatus, setDragOverStatus] = useState<RepairOrderStatus | null>(null)
  const [isValidDrop, setIsValidDrop] = useState<boolean>(true)
  const [validationMessage, setValidationMessage] = useState<string>('')
  const [localOrders, setLocalOrders] = useState<RepairOrder[]>(orders)
  const [dropIndicatorById, setDropIndicatorById] = useState<
    Record<string, 'top' | 'bottom'>
  >({})

  const handleDragStart = (event: DragStartEvent) => {
    setActiveId(event.active.id as string)
  }

  const handleDragOver = (event: DragOverEvent) => {
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
    const overStatus = COLUMNS.find((col) => col.status === overId)?.status
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
        const activeCenterY = activeInitial.top + activeInitial.height / 2 + event.delta.y
        const position: 'top' | 'bottom' = activeCenterY < overCenterY ? 'top' : 'bottom'
        setDropIndicatorById({ [overId]: position })
      }
    } else {
      setDropIndicatorById({})
    }
  }

  const handleDragEnd = (event: DragEndEvent) => {
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
    const overStatus = COLUMNS.find((col) => col.status === overId)?.status
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
      onStatusChange(orderId, targetStatus)
      return
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
  }

  return {
    activeId,
    dragOverStatus,
    isValidDrop,
    validationMessage,
    localOrders,
    dropIndicatorById,
    handleDragStart,
    handleDragOver,
    handleDragEnd,
    groupedOrders: useMemo(() => {
      return COLUMNS.reduce(
        (acc, col) => {
          acc[col.status] = localOrders.filter((o) => o.status === col.status)
          return acc
        },
        {} as Record<RepairOrderStatus, RepairOrder[]>,
      )
    }, [localOrders]),
  }
}
