import type { RepairOrderStatus, RepairOrder } from './types.js'

export const STAGE_ORDER: RepairOrderStatus[] = [
  'NEW',
  'AWAITING_APPROVAL',
  'IN_PROGRESS',
  'WAITING_PARTS',
  'COMPLETED',
]

export const ALLOWED_TRANSITIONS: Record<RepairOrderStatus, RepairOrderStatus[]> = {
  NEW: ['AWAITING_APPROVAL', 'IN_PROGRESS'],
  AWAITING_APPROVAL: ['IN_PROGRESS', 'NEW'],
  IN_PROGRESS: ['WAITING_PARTS', 'COMPLETED', 'AWAITING_APPROVAL'],
  WAITING_PARTS: ['IN_PROGRESS'],
  COMPLETED: [],
}

export interface ValidationResult {
  allowed: boolean
  reason?: string
}

export function canTransition(
  from: RepairOrderStatus,
  to: RepairOrderStatus,
  order?: Partial<RepairOrder>,
): ValidationResult {
  if (from === to) {
    return { allowed: true }
  }

  const allowedTransitions = ALLOWED_TRANSITIONS[from]

  if (!allowedTransitions || !allowedTransitions.includes(to)) {
    return {
      allowed: false,
      reason: `Cannot move from ${from} to ${to}. Allowed transitions: ${
        allowedTransitions?.join(', ') || 'none'
      }`,
    }
  }

  // Business rule: Cannot move to IN_PROGRESS without assigned technician
  if (to === 'IN_PROGRESS' && !order?.assignedTech) {
    return {
      allowed: false,
      reason: 'Assign a technician before starting work',
    }
  }

  // Business rule: Cannot move to COMPLETED without customer approval
  if (to === 'COMPLETED' && !order?.approvedByCustomer) {
    return {
      allowed: false,
      reason: 'Customer approval required before marking as completed',
    }
  }

  return { allowed: true }
}
