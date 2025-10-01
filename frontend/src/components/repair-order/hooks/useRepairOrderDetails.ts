import { useSuspenseQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import type { RepairOrder } from '@shared/types'
import type { UpdateRepairOrderInput, CreateRepairOrderInput } from '@shared/validation'
import { REPAIR_ORDER_LABELS, API_ENDPOINTS } from '@shared/constants'

async function fetchRepairOrder(id: string): Promise<RepairOrder> {
  const response = await fetch(API_ENDPOINTS.REPAIR_ORDERS.BY_ID(id))
  if (!response.ok) {
    throw new Error(REPAIR_ORDER_LABELS.FAILED_TO_FETCH)
  }
  return response.json()
}

async function createRepairOrder(data: CreateRepairOrderInput): Promise<RepairOrder> {
  const response = await fetch(API_ENDPOINTS.REPAIR_ORDERS.BASE, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || REPAIR_ORDER_LABELS.FAILED_TO_CREATE)
  }
  return response.json()
}

async function updateRepairOrder(
  id: string,
  data: UpdateRepairOrderInput,
): Promise<RepairOrder> {
  const response = await fetch(API_ENDPOINTS.REPAIR_ORDERS.BY_ID(id), {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.message || REPAIR_ORDER_LABELS.FAILED_TO_UPDATE)
  }
  return response.json()
}

async function deleteRepairOrder(id: string): Promise<void> {
  const response = await fetch(API_ENDPOINTS.REPAIR_ORDERS.BY_ID(id), {
    method: 'DELETE',
  })
  if (!response.ok) {
    throw new Error(REPAIR_ORDER_LABELS.FAILED_TO_DELETE)
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

export function useCreateRepairOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreateRepairOrderInput) => createRepairOrder(data),
    onSuccess: (newOrder) => {
      queryClient.setQueryData(['repairOrder', newOrder.id], newOrder)
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
    },
  })
}
