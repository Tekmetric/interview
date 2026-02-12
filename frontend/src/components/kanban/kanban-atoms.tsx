'use client'

import type { HTMLAttributes, ReactNode } from 'react'
import { createPortal } from 'react-dom'
import { DragOverlay, useDroppable } from '@dnd-kit/core'
import { SortableContext, useSortable } from '@dnd-kit/sortable'
import { CSS } from '@dnd-kit/utilities'

import { Card } from '@/components/ui/card'
import { ScrollArea, ScrollBar } from '@/components/ui/scroll-area'
import { cn } from '@/lib/utils'
import { InvalidDropOverlay } from './invalid-drop-overlay'

export type { DragEndEvent } from '@dnd-kit/core'

type KanbanItemProps = {
  id: string
  name: string
}

export type KanbanBoardProps = {
  id: string
  children: ReactNode
  className?: string
}

type KanbanColumnProps<TColumn extends { id: string }> = {
  children: ReactNode
  column: TColumn
  isValidDropZone?: boolean
  validationMessage?: string
  // When dragging over a child card inside the column, the column's own
  // droppable may not report isOver. This derived flag enables column-level
  // feedback in that case as well.
  isBeingHoveredDuringDrag?: boolean
  dropIndicatorById?: Record<string, 'top' | 'bottom'>
}

export function KanbanColumn<TColumn extends { id: string }>({
  children,
  column,
  isValidDropZone = true,
  validationMessage,
  isBeingHoveredDuringDrag = false,
}: KanbanColumnProps<TColumn>) {
  if (!column) return null

  const { id } = column
  const { isOver, setNodeRef } = useDroppable({ id })
  const isColumnOver = isOver || isBeingHoveredDuringDrag

  return (
    <div
      ref={setNodeRef}
      className={`relative flex flex-col overflow-hidden rounded-lg transition-all ${
        isColumnOver && !isValidDropZone ? 'animate-pulse ring-2 ring-red-400' : ''
      }`}
      aria-label={`${(column as any).title ?? 'Column'} column`}
      role='region'
    >
      {isColumnOver && !isValidDropZone && validationMessage && (
        <InvalidDropOverlay validationMessage={validationMessage} />
      )}

      {children}
    </div>
  )
}

export type KanbanCardProps<T extends KanbanItemProps = KanbanItemProps> = T & {
  children?: ReactNode | ((ctx: { isDragging: boolean }) => ReactNode)
  className?: string
}

export const KanbanCard = <T extends KanbanItemProps = KanbanItemProps>({
  id,
  name,
  children,
  className,
}: KanbanCardProps<T>) => {
  const { attributes, listeners, setNodeRef, transition, transform, isDragging } =
    useSortable({ id })

  const style = {
    transition,
    transform: CSS.Transform.toString(transform),
  }

  const renderedChildren =
    typeof children === 'function'
      ? children({ isDragging })
      : (children ?? <p className='m-0 text-sm font-medium'>{name}</p>)

  return (
    <div style={style} ref={setNodeRef}>
      <Card
        className={cn(
          'cursor-grab gap-4 rounded-md p-3 shadow-sm',
          isDragging && 'pointer-events-none cursor-grabbing opacity-30',
          className,
        )}
        {...listeners}
        {...attributes}
      >
        {renderedChildren}
      </Card>
    </div>
  )
}

export type KanbanCardsProps<T extends KanbanItemProps = KanbanItemProps> = Omit<
  HTMLAttributes<HTMLDivElement>,
  'children' | 'id'
> & {
  children: (item: T) => ReactNode
  id: string
  data: T[]
  columnField?: string
}

export const KanbanCardsList = <T extends KanbanItemProps = KanbanItemProps>({
  children,
  className,
  data,
  columnField = 'column',
  ...props
}: KanbanCardsProps<T>) => {
  const filteredData = data.filter(
    (item) => (item as Record<string, unknown>)[columnField] === props.id,
  )
  const items = filteredData.map((item) => item.id)

  return (
    <ScrollArea className='h-[calc(100vh-320px)] sm:h-[calc(100vh-280px)] lg:h-[calc(100vh-220px)]'>
      <SortableContext items={items}>
        <div className={cn('flex flex-grow flex-col gap-2 p-2', className)} {...props}>
          {filteredData.map(children)}
        </div>
      </SortableContext>
      <ScrollBar orientation='vertical' />
    </ScrollArea>
  )
}

export type KanbanHeaderProps = HTMLAttributes<HTMLDivElement>

export const KanbanHeader = ({ className, ...props }: KanbanHeaderProps) => (
  <div className={cn('m-0 p-2 text-sm font-semibold', className)} {...props} />
)

export type KanbanContainerProps = {
  children: ReactNode
  className?: string
}

export const KanbanContainer = ({ children, className }: KanbanContainerProps) => (
  <ScrollArea className='w-full'>
    <div className={cn('flex gap-3 bg-gray-50/50 p-3', className)}>{children}</div>
    <ScrollBar orientation='horizontal' />
  </ScrollArea>
)

export type KanbanOverlayProps = {
  children: ReactNode
}

export const KanbanOverlay = ({ children }: KanbanOverlayProps) => {
  if (typeof window === 'undefined') {
    return null
  }

  return createPortal(<DragOverlay>{children}</DragOverlay>, document.body)
}
