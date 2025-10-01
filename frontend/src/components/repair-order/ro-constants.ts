import type { RepairOrderStatus, Priority } from '@shared/types'

export const STATUS_COLORS: Record<RepairOrderStatus, string> = {
  NEW: 'bg-blue-500',
  AWAITING_APPROVAL: 'bg-amber-500',
  IN_PROGRESS: 'bg-indigo-500',
  WAITING_PARTS: 'bg-orange-500',
  COMPLETED: 'bg-green-500',
}

export const PRIORITY_COLORS: Record<Priority, string> = {
  HIGH: 'border-red-500 text-red-700',
  NORMAL: 'border-gray-300 text-gray-600',
}
