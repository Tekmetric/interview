import type { RepairOrderStatus, Priority } from '@shared/types'
import { RO_STATUS, KANBAN_LABELS } from '@shared/constants'

export const STATUS_COLORS: Record<RepairOrderStatus, string> = {
  [RO_STATUS.NEW]: 'bg-blue-500',
  [RO_STATUS.AWAITING_APPROVAL]: 'bg-amber-500',
  [RO_STATUS.IN_PROGRESS]: 'bg-indigo-500',
  [RO_STATUS.WAITING_PARTS]: 'bg-orange-500',
  [RO_STATUS.COMPLETED]: 'bg-green-500',
}

export const PRIORITY_COLORS: Record<Priority, string> = {
  HIGH: 'border-red-500 text-red-700',
  NORMAL: 'border-gray-300 text-gray-600',
}

export const STATUS_CONFIG: Record<
  RepairOrderStatus,
  { bg: string; text: string; border: string; label: string }
> = {
  [RO_STATUS.NEW]: {
    bg: 'bg-blue-50',
    text: 'text-blue-700',
    border: 'border-blue-200',
    label: KANBAN_LABELS.STATUS.NEW,
  },
  [RO_STATUS.AWAITING_APPROVAL]: {
    bg: 'bg-amber-50',
    text: 'text-amber-700',
    border: 'border-amber-200',
    label: KANBAN_LABELS.STATUS.AWAITING_APPROVAL,
  },
  [RO_STATUS.IN_PROGRESS]: {
    bg: 'bg-indigo-50',
    text: 'text-indigo-700',
    border: 'border-indigo-200',
    label: KANBAN_LABELS.STATUS.IN_PROGRESS,
  },
  [RO_STATUS.WAITING_PARTS]: {
    bg: 'bg-purple-50',
    text: 'text-purple-700',
    border: 'border-purple-200',
    label: KANBAN_LABELS.STATUS.WAITING_PARTS,
  },
  [RO_STATUS.COMPLETED]: {
    bg: 'bg-green-50',
    text: 'text-green-700',
    border: 'border-green-200',
    label: KANBAN_LABELS.STATUS.COMPLETED,
  },
}
