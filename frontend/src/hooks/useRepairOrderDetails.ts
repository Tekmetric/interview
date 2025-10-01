import { useSuspenseQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import type { RepairOrder } from '@shared/types'
import type { UpdateRepairOrderInput } from '@shared/validation'

async function fetchRepairOrder(id: string): Promise<RepairOrder> {
  const response = await fetch(`/api/repairOrders/${id}`)
  if (!response.ok) {
    throw new Error('Failed to fetch repair order')
  }
  return response.json()
}

async function updateRepairOrder(
  id: string,
  data: UpdateRepairOrderInput,
): Promise<RepairOrder> {
  const response = await fetch(`/api/repairOrders/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || 'Failed to update repair order')
  }
  return response.json()
}

async function deleteRepairOrder(id: string): Promise<void> {
  const response = await fetch(`/api/repairOrders/${id}`, {
    method: 'DELETE',
  })
  if (!response.ok) {
    throw new Error('Failed to delete repair order')
  }
}

export function useRepairOrder(id: string | null) {
  return useSuspenseQuery({
    queryKey: ['repairOrder', id],
    queryFn: () => fetchRepairOrder(id!),
  })
}

export function useUpdateRepairOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateRepairOrderInput }) =>
      updateRepairOrder(id, data),
    onSuccess: (updatedOrder) => {
      // Update both the single order cache and the list cache
      queryClient.setQueryData(['repairOrder', updatedOrder.id], updatedOrder)
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
    },
  })
}

export function useDeleteRepairOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: string) => deleteRepairOrder(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
    },
  })
}
