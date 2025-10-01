import { useQuery } from '@tanstack/react-query'
import type { RepairOrder } from '@shared/types'
import { DASHBOARD_LABELS, API_ENDPOINTS } from '@shared/constants'

async function fetchOverdueOrders(limit: number = 5): Promise<RepairOrder[]> {
  const res = await fetch(`${API_ENDPOINTS.REPAIR_ORDERS.OVERDUE}?limit=${limit}`)
  if (!res.ok) throw new Error(DASHBOARD_LABELS.FAILED_TO_FETCH_OVERDUE)
  return res.json()
}

async function fetchRecentOrders(limit: number = 5): Promise<RepairOrder[]> {
  const res = await fetch(`${API_ENDPOINTS.REPAIR_ORDERS.RECENT}?limit=${limit}`)
  if (!res.ok) throw new Error(DASHBOARD_LABELS.FAILED_TO_FETCH_RECENT)
  return res.json()
}

export function useOverdueOrders(limit: number = 5) {
  return useQuery({
    queryKey: ['repairOrders', 'overdue', limit],
    queryFn: () => fetchOverdueOrders(limit),
    refetchInterval: 30000,
  })
}

export function useRecentOrders(limit: number = 5) {
  return useQuery({
    queryKey: ['repairOrders', 'recent', limit],
    queryFn: () => fetchRecentOrders(limit),
    refetchInterval: 30000,
  })
}
