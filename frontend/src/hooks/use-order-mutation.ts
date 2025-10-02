import { useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import type { RepairOrder, RepairOrderStatus } from '@shared/types'
import { API_ENDPOINTS, REPAIR_ORDER_LABELS } from '@shared/constants'

/**
 * API function to update repair order status.
 */
async function updateOrderStatus(orderId: string, status: RepairOrderStatus) {
  const res = await fetch(API_ENDPOINTS.REPAIR_ORDERS.BY_ID(orderId), {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ status }),
  })

  if (!res.ok) {
    const error = await res.json()
    throw new Error(error.message || REPAIR_ORDER_LABELS.FAILED_TO_UPDATE_STATUS)
  }

  return res.json()
}

type UseOrderMutationReturn = {
  updateOrderStatus: (orderId: string, newStatus: RepairOrderStatus) => void
  isUpdating: boolean
}

/**
 * Business logic for optimistic status updates with automatic rollback on error.
 * Handles TanStack Query cache updates, error handling, and success notifications.
 */
export function useOrderMutation(): UseOrderMutationReturn {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: ({ orderId, status }: { orderId: string; status: RepairOrderStatus }) =>
      updateOrderStatus(orderId, status),
    onMutate: async ({ orderId, status }) => {
      await queryClient.cancelQueries({ queryKey: ['repairOrders'] })

      const previousOrders = queryClient.getQueryData<RepairOrder[]>(['repairOrders'])

      if (previousOrders) {
        queryClient.setQueryData<RepairOrder[]>(
          ['repairOrders'],
          previousOrders.map((order) =>
            order.id === orderId ? { ...order, status } : order,
          ),
        )
      }

      return { previousOrders }
    },
    onError: (err: Error, _variables, context) => {
      if (context?.previousOrders) {
        queryClient.setQueryData(['repairOrders'], context.previousOrders)
      }
      toast.error(err.message || REPAIR_ORDER_LABELS.FAILED_TO_UPDATE_STATUS)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
      toast.success(REPAIR_ORDER_LABELS.STATUS_UPDATED)
    },
  })

  const handleStatusChange = (orderId: string, newStatus: RepairOrderStatus) => {
    mutation.mutate({ orderId, status: newStatus })
  }

  return {
    updateOrderStatus: handleStatusChange,
    isUpdating: mutation.isPending,
  }
}
