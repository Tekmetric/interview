import type { RepairOrder } from '@shared/types'
import { RO_STATUS } from '@shared/constants'

export interface KPIMetrics {
  totalWIP: number
  overdueCount: number
  waitingPartsCount: number
  awaitingApprovalCount: number
}

/**
 * Calculate KPI metrics from repair orders
 * @param orders - Array of repair orders
 * @returns KPI metrics object
 */
export function calculateKPIs(orders: RepairOrder[]): KPIMetrics {
  const now = new Date()

  return {
    totalWIP: orders.filter((order) => order.status !== RO_STATUS.COMPLETED).length,
    overdueCount: orders.filter(
      (order) =>
        order.dueTime && new Date(order.dueTime) < now && order.status !== RO_STATUS.COMPLETED,
    ).length,
    waitingPartsCount: orders.filter((order) => order.status === RO_STATUS.WAITING_PARTS).length,
    awaitingApprovalCount: orders.filter((order) => order.status === RO_STATUS.AWAITING_APPROVAL)
      .length,
  }
}
