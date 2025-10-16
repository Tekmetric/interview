import type { DragEndEvent, DragOverEvent, DragStartEvent } from '@dnd-kit/core'

import {
  KeyboardSensor,
  MouseSensor,
  TouchSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core'
import { arrayMove } from '@dnd-kit/sortable'
import type { ReactNode } from 'react'
import { useState } from 'react'

export type { DragEndEvent } from '@dnd-kit/core'

type KanbanItemProps = {
  id: string
  name: string
  [key: string]: any
}

export type KanbanBoardProps = {
  id: string
  children: ReactNode
  className?: string
}

export type KanbanCardProps<T extends KanbanItemProps = KanbanItemProps> = T & {
  children?: ReactNode | ((ctx: { isDragging: boolean }) => ReactNode)
  className?: string
}

type UseKanbanOptions<
  T extends KanbanItemProps = KanbanItemProps,
  C extends { id: string } = { id: string },
> = {
  columns: C[]
  data: T[]
  columnField?: string
  onDataChange?: (data: T[]) => void
  onDragStart?: (event: DragStartEvent) => void
  onDragEnd?: (event: DragEndEvent) => void
  onDragOver?: (event: DragOverEvent) => void
}

type UseKanbanReturn<T extends KanbanItemProps = KanbanItemProps> = {
  sensors: ReturnType<typeof useSensors>
  activeCardId: string | null
  activeCard: T | undefined
  handleDragStart: (event: DragStartEvent) => void
  handleDragOver: (event: DragOverEvent) => void
  handleDragEnd: (event: DragEndEvent) => void
}

export const useKanban = <
  T extends KanbanItemProps = KanbanItemProps,
  C extends { id: string } = { id: string },
>({
  columns,
  data,
  columnField = 'column',
  onDataChange,
  onDragStart,
  onDragEnd,
  onDragOver,
}: UseKanbanOptions<T, C>): UseKanbanReturn<T> => {
  const [activeCardId, setActiveCardId] = useState<string | null>(null)

  const sensors = useSensors(
    // Require slight movement before activating drag to allow normal clicks
    useSensor(MouseSensor, {
      activationConstraint: { distance: 8 },
    }),
    // Small press delay on touch to avoid accidental drags on taps
    useSensor(TouchSensor, {
      activationConstraint: { delay: 150, tolerance: 5 },
    }),
    useSensor(KeyboardSensor),
  )

  const activeCard = data.find((item) => item.id === activeCardId)

  const handleDragStart = (event: DragStartEvent) => {
    const card = data.find((item) => item.id === event.active.id)
    if (card) {
      setActiveCardId(event.active.id as string)
    }
    onDragStart?.(event)
  }

  const handleDragOver = (event: DragOverEvent) => {
    const { active, over } = event

    if (!over) {
      return
    }

    const activeItem = data.find((item) => item.id === active.id)
    const overItem = data.find((item) => item.id === over.id)

    if (!activeItem) {
      return
    }

    const activeColumn = (activeItem as any)[columnField]
    const overColumn =
      (overItem as any)?.[columnField] ||
      columns.find((col) => col.id === over.id)?.id ||
      columns[0]?.id

    if (activeColumn !== overColumn) {
      let newData = [...data]
      const activeIndex = newData.findIndex((item) => item.id === active.id)
      const overIndex = newData.findIndex((item) => item.id === over.id)

      ;(newData[activeIndex] as any)[columnField] = overColumn as string
      newData = arrayMove(newData, activeIndex, overIndex >= 0 ? overIndex : activeIndex)

      onDataChange?.(newData)
    }

    onDragOver?.(event)
  }

  const handleDragEnd = (event: DragEndEvent) => {
    setActiveCardId(null)

    onDragEnd?.(event)

    const { active, over } = event

    if (!over || active.id === over.id) {
      return
    }

    let newData = [...data]

    const oldIndex = newData.findIndex((item) => item.id === active.id)
    const newIndex = newData.findIndex((item) => item.id === over.id)

    if (oldIndex === -1 || newIndex === -1) {
      return
    }

    newData = arrayMove(newData, oldIndex, newIndex)

    onDataChange?.(newData)
  }

  return {
    sensors,
    activeCardId,
    activeCard,
    handleDragStart,
    handleDragOver,
    handleDragEnd,
  }
}
