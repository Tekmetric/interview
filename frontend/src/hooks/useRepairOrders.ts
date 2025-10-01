import { useSuspenseQuery } from '@tanstack/react-query'
import type { RepairOrder } from '@shared/types'

async function fetchRepairOrders(): Promise<RepairOrder[]> {
  const response = await fetch('/api/repairOrders')
  if (!response.ok) {
    throw new Error('Failed to fetch repair orders')
  }
  return response.json()
}

export function useRepairOrders() {
  return useSuspenseQuery({
    queryKey: ['repairOrders'],
    queryFn: fetchRepairOrders,
  })
}
