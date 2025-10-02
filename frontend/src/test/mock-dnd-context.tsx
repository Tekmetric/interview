import React from 'react'
import { DndContext, DragEndEvent, DndContextProps } from '@dnd-kit/core'

export const MockDndContext = ({ children, onDragEnd }: DndContextProps) => {
  const handleDragEnd = (event: DragEndEvent) => {
    if (onDragEnd) {
      onDragEnd(event)
    }
  }

  return <DndContext onDragEnd={handleDragEnd}>{children}</DndContext>
}
