import { useSuspenseQuery } from '@tanstack/react-query'
import type { RepairOrder } from '@shared/types'
import { REPAIR_ORDER_LABELS, API_ENDPOINTS } from '@shared/constants'

async function fetchRepairOrders(): Promise<RepairOrder[]> {
  const response = await fetch(API_ENDPOINTS.REPAIR_ORDERS.BASE)
  if (!response.ok) {
    throw new Error(REPAIR_ORDER_LABELS.ERROR_LOADING_LIST)
  }
  return response.json()
}

export function useRepairOrders() {
  return useSuspenseQuery({
    queryKey: ['repairOrders'],
    queryFn: fetchRepairOrders,
  })
}
