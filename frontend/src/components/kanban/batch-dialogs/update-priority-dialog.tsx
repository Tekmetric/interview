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
import type { RepairOrder } from '@shared/types'

type UpdatePriorityDialogProps = {
  open: boolean
  onOpenChange: (open: boolean) => void
  selectedOrderIds: string[]
  onSuccess: () => void
}

async function updatePriorityForOrders(orderIds: string[], priority: 'HIGH' | 'NORMAL') {
  const results = await Promise.allSettled(
    orderIds.map((orderId) =>
      fetch(API_ENDPOINTS.REPAIR_ORDERS.BY_ID(orderId), {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ priority }),
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

export function UpdatePriorityDialog({
  open,
  onOpenChange,
  selectedOrderIds,
  onSuccess,
}: UpdatePriorityDialogProps) {
  const queryClient = useQueryClient()
  const [selectedPriority, setSelectedPriority] = useState<'HIGH' | 'NORMAL' | ''>('')

  const mutation = useMutation({
    mutationFn: (priority: 'HIGH' | 'NORMAL') =>
      updatePriorityForOrders(selectedOrderIds, priority),
    onMutate: async (priority) => {
      await queryClient.cancelQueries({ queryKey: ['repairOrders'] })

      const previousOrders = queryClient.getQueryData<RepairOrder[]>(['repairOrders'])

      if (previousOrders) {
        queryClient.setQueryData<RepairOrder[]>(
          ['repairOrders'],
          previousOrders.map((order) =>
            selectedOrderIds.includes(order.id) ? { ...order, priority } : order,
          ),
        )
      }

      return { previousOrders }
    },
    onError: (err: Error, _variables, context) => {
      if (context?.previousOrders) {
        queryClient.setQueryData(['repairOrders'], context.previousOrders)
      }
      toast.error(err.message || 'Failed to update priority')
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
      toast.success(
        `Updated priority for ${selectedOrderIds.length} order${selectedOrderIds.length === 1 ? '' : 's'}`,
      )
      onSuccess()
      onOpenChange(false)
      setSelectedPriority('')
    },
  })

  const handleSubmit = () => {
    if (!selectedPriority) {
      toast.error('Please select a priority')
      return
    }
    mutation.mutate(selectedPriority as 'HIGH' | 'NORMAL')
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Update Priority</DialogTitle>
          <DialogDescription>
            Update the priority for {selectedOrderIds.length} selected order
            {selectedOrderIds.length === 1 ? '' : 's'}
          </DialogDescription>
        </DialogHeader>

        <Select
          value={selectedPriority}
          onValueChange={(value) => setSelectedPriority(value as 'HIGH' | 'NORMAL' | '')}
        >
          <SelectTrigger>
            <SelectValue placeholder='Select priority' />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value='HIGH'>High Priority</SelectItem>
            <SelectItem value='NORMAL'>Normal Priority</SelectItem>
          </SelectContent>
        </Select>

        <DialogFooter>
          <Button variant='outline' onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button
            onClick={handleSubmit}
            disabled={mutation.isPending || !selectedPriority}
          >
            {mutation.isPending ? 'Updating...' : 'Update Priority'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
