import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'

import { useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { API_ENDPOINTS } from '@shared/constants'
import type { RepairOrder } from '@shared/types'

type DeleteOrdersDialogProps = {
  open: boolean
  onOpenChange: (open: boolean) => void
  selectedOrderIds: string[]
  onSuccess: () => void
}

async function deleteOrders(orderIds: string[]) {
  const results = await Promise.allSettled(
    orderIds.map((orderId) =>
      fetch(API_ENDPOINTS.REPAIR_ORDERS.BY_ID(orderId), {
        method: 'DELETE',
      }).then((res) => {
        if (!res.ok) throw new Error(`Failed to delete order ${orderId}`)
        return res.json()
      }),
    ),
  )

  const failures = results.filter((r) => r.status === 'rejected')
  if (failures.length > 0) {
    throw new Error(`Failed to delete ${failures.length} order(s)`)
  }

  return results
}

export function DeleteOrdersDialog({
  open,
  onOpenChange,
  selectedOrderIds,
  onSuccess,
}: DeleteOrdersDialogProps) {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: () => deleteOrders(selectedOrderIds),
    onMutate: async () => {
      // Cancel any outgoing refetches
      await queryClient.cancelQueries({ queryKey: ['repairOrders'] })

      // Snapshot previous value
      const previousOrders = queryClient.getQueryData<RepairOrder[]>(['repairOrders'])

      // Optimistically remove from cache
      if (previousOrders) {
        queryClient.setQueryData<RepairOrder[]>(
          ['repairOrders'],
          previousOrders.filter((order) => !selectedOrderIds.includes(order.id)),
        )
      }

      return { previousOrders }
    },
    onError: (err: Error, _variables, context) => {
      // Rollback on error
      if (context?.previousOrders) {
        queryClient.setQueryData(['repairOrders'], context.previousOrders)
      }
      toast.error(err.message || 'Failed to delete orders')
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
      toast.success(
        `Deleted ${selectedOrderIds.length} order${selectedOrderIds.length === 1 ? '' : 's'}`,
      )
      onSuccess()
      onOpenChange(false)
    },
  })

  return (
    <AlertDialog open={open} onOpenChange={onOpenChange}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Are you sure?</AlertDialogTitle>
          <AlertDialogDescription>
            This will permanently delete {selectedOrderIds.length} order
            {selectedOrderIds.length === 1 ? '' : 's'}. This action cannot be undone.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Cancel</AlertDialogCancel>
          <AlertDialogAction
            onClick={() => mutation.mutate()}
            disabled={mutation.isPending}
            className='bg-red-600 hover:bg-red-700'
          >
            {mutation.isPending ? 'Deleting...' : 'Delete'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}
