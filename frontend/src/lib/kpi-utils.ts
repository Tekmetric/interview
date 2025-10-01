import type { RepairOrder } from '@shared/types'

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
    totalWIP: orders.filter((order) => order.status !== 'COMPLETED').length,
    overdueCount: orders.filter(
      (order) =>
        order.dueTime && new Date(order.dueTime) < now && order.status !== 'COMPLETED',
    ).length,
    waitingPartsCount: orders.filter((order) => order.status === 'WAITING_PARTS').length,
    awaitingApprovalCount: orders.filter((order) => order.status === 'AWAITING_APPROVAL')
      .length,
  }
}
