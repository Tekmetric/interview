export const RO_STATUS = {
  NEW: 'NEW',
  AWAITING_APPROVAL: 'AWAITING_APPROVAL',
  IN_PROGRESS: 'IN_PROGRESS',
  WAITING_PARTS: 'WAITING_PARTS',
  COMPLETED: 'COMPLETED',
} as const

export const REPAIR_ORDER_STATUSES = Object.values(RO_STATUS)

export const PRIORITY = {
  HIGH: 'HIGH',
  NORMAL: 'NORMAL',
} as const

export const PRIORITIES = Object.values(PRIORITY)
