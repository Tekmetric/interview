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
    onMutate: async ({ id, data }) => {
      await queryClient.cancelQueries({ queryKey: ['repairOrders'] })
      await queryClient.cancelQueries({ queryKey: ['repairOrder', id] })

      const previousOrders = queryClient.getQueryData<RepairOrder[]>(['repairOrders'])
      const previousOrder = queryClient.getQueryData<RepairOrder>(['repairOrder', id])

      if (previousOrder) {
        const optimisticOrder = { ...previousOrder, ...data }
        queryClient.setQueryData(['repairOrder', id], optimisticOrder)
      }

      if (previousOrders) {
        queryClient.setQueryData(
          ['repairOrders'],
          previousOrders.map((order) =>
            order.id === id ? { ...order, ...data } : order,
          ),
        )
      }

      return { previousOrders, previousOrder }
    },
    onError: (_err, { id }, context) => {
      if (context?.previousOrders) {
        queryClient.setQueryData(['repairOrders'], context.previousOrders)
      }
      if (context?.previousOrder) {
        queryClient.setQueryData(['repairOrder', id], context.previousOrder)
      }
    },
    onSuccess: (updatedOrder) => {
      queryClient.setQueryData(['repairOrder', updatedOrder.id], updatedOrder)
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
    },
  })
}

export function useDeleteRepairOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: string) => deleteRepairOrder(id),
    onMutate: async (id) => {
      await queryClient.cancelQueries({ queryKey: ['repairOrders'] })

      const previousOrders = queryClient.getQueryData<RepairOrder[]>(['repairOrders'])

      if (previousOrders) {
        queryClient.setQueryData(
          ['repairOrders'],
          previousOrders.filter((order) => order.id !== id),
        )
      }

      return { previousOrders }
    },
    onError: (_err, _id, context) => {
      if (context?.previousOrders) {
        queryClient.setQueryData(['repairOrders'], context.previousOrders)
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
    },
  })
}

export function useCreateRepairOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreateRepairOrderInput) => createRepairOrder(data),
    onMutate: async (data) => {
      await queryClient.cancelQueries({ queryKey: ['repairOrders'] })

      const previousOrders = queryClient.getQueryData<RepairOrder[]>(['repairOrders'])

      const optimisticOrder = {
        id: `temp-${Date.now()}`,
        ...data,
        status: 'NEW' as const,
        assignedTech: null,
        approvedByCustomer: false,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      } as RepairOrder

      if (previousOrders) {
        queryClient.setQueryData<RepairOrder[]>(
          ['repairOrders'],
          [optimisticOrder, ...previousOrders],
        )
      }

      return { previousOrders, optimisticOrder }
    },
    onError: (_err, _data, context) => {
      if (context?.previousOrders) {
        queryClient.setQueryData(['repairOrders'], context.previousOrders)
      }
    },
    onSuccess: (newOrder, _data, context) => {
      const previousOrders = queryClient.getQueryData<RepairOrder[]>(['repairOrders'])
      if (previousOrders && context?.optimisticOrder) {
        queryClient.setQueryData<RepairOrder[]>(
          ['repairOrders'],
          previousOrders.map((order) =>
            order.id === context.optimisticOrder.id ? newOrder : order,
          ),
        )
      }
      queryClient.setQueryData(['repairOrder', newOrder.id], newOrder)
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
    },
  })
}
