import type { RepairOrder, RepairOrderStatus } from '@shared/types'
import type { ReactNode } from 'react'

export type KanbanColumnProps = {
  status: RepairOrderStatus
  orders: RepairOrder[]
  title: string | ReactNode
  color: string
} & Record<string, unknown>
