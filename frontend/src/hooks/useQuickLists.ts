import { useQuery } from '@tanstack/react-query'
import type { RepairOrder } from '@shared/types'

async function fetchOverdueOrders(limit: number = 5): Promise<RepairOrder[]> {
  const res = await fetch(`/api/repairOrders/overdue?limit=${limit}`)
  if (!res.ok) throw new Error('Failed to fetch overdue orders')
  return res.json()
}

async function fetchRecentOrders(limit: number = 5): Promise<RepairOrder[]> {
  const res = await fetch(`/api/repairOrders/recent?limit=${limit}`)
  if (!res.ok) throw new Error('Failed to fetch recent orders')
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
