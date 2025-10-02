import { useState } from 'react'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Button } from '@/components/ui/button'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { API_ENDPOINTS } from '@shared/constants'
import { STATUS_CONFIG } from '@/components/repair-order/ro-constants'
import type { RepairOrder, RepairOrderStatus } from '@shared/types'

type ChangeStatusDialogProps = {
  open: boolean
  onOpenChange: (open: boolean) => void
  selectedOrderIds: string[]
  onSuccess: () => void
}

async function changeStatusForOrders(orderIds: string[], status: RepairOrderStatus) {
  const results = await Promise.allSettled(
    orderIds.map((orderId) =>
      fetch(API_ENDPOINTS.REPAIR_ORDERS.BY_ID(orderId), {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status }),
      }).then((res) => {
        if (!res.ok) throw new Error(`Failed to update order ${orderId}`)
        return res.json()
      }),
    ),
  )

  const failures = results.filter((r) => r.status === 'rejected')
  if (failures.length > 0) {
    throw new Error(`Failed to update ${failures.length} order(s)`)
  }

  return results
}

export function ChangeStatusDialog({
  open,
  onOpenChange,
  selectedOrderIds,
  onSuccess,
}: ChangeStatusDialogProps) {
  const queryClient = useQueryClient()
  const [selectedStatus, setSelectedStatus] = useState<RepairOrderStatus | ''>('')

  const mutation = useMutation({
    mutationFn: (status: RepairOrderStatus) =>
      changeStatusForOrders(selectedOrderIds, status),
    onMutate: async (status) => {
      // Cancel any outgoing refetches
      await queryClient.cancelQueries({ queryKey: ['repairOrders'] })

      // Snapshot previous value
      const previousOrders = queryClient.getQueryData<RepairOrder[]>(['repairOrders'])

      // Optimistically update cache
      if (previousOrders) {
        queryClient.setQueryData<RepairOrder[]>(
          ['repairOrders'],
          previousOrders.map((order) =>
            selectedOrderIds.includes(order.id) ? { ...order, status } : order,
          ),
        )
      }

      return { previousOrders }
    },
    onError: (err: Error, _variables, context) => {
      // Rollback on error
      if (context?.previousOrders) {
        queryClient.setQueryData(['repairOrders'], context.previousOrders)
      }
      toast.error(err.message || 'Failed to change status')
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
      toast.success(
        `Changed status for ${selectedOrderIds.length} order${selectedOrderIds.length === 1 ? '' : 's'}`,
      )
      onSuccess()
      onOpenChange(false)
      setSelectedStatus('')
    },
  })

  const handleSubmit = () => {
    if (!selectedStatus) {
      toast.error('Please select a status')
      return
    }
    mutation.mutate(selectedStatus as RepairOrderStatus)
  }

  const statuses = Object.entries(STATUS_CONFIG) as [
    RepairOrderStatus,
    (typeof STATUS_CONFIG)[RepairOrderStatus],
  ][]

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Change Status</DialogTitle>
          <DialogDescription>
            Change the status for {selectedOrderIds.length} selected order
            {selectedOrderIds.length === 1 ? '' : 's'}
          </DialogDescription>
        </DialogHeader>

        <Select value={selectedStatus} onValueChange={setSelectedStatus}>
          <SelectTrigger>
            <SelectValue placeholder='Select status' />
          </SelectTrigger>
          <SelectContent>
            {statuses.map(([key, config]) => (
              <SelectItem key={key} value={key}>
                {config.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>

        <DialogFooter>
          <Button variant='outline' onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button onClick={handleSubmit} disabled={mutation.isPending || !selectedStatus}>
            {mutation.isPending ? 'Updating...' : 'Update Status'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
