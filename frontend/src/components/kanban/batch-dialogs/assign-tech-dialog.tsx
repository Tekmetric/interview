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
import { useTechnicians } from '@/components/technician/hooks/useTechnicians'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { API_ENDPOINTS } from '@shared/constants'

type AssignTechDialogProps = {
  open: boolean
  onOpenChange: (open: boolean) => void
  selectedOrderIds: string[]
  onSuccess: () => void
}

async function assignTechToOrders(orderIds: string[], techId: string) {
  const results = await Promise.allSettled(
    orderIds.map((orderId) =>
      fetch(API_ENDPOINTS.REPAIR_ORDERS.BY_ID(orderId), {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ assignedTech: techId }),
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

export function AssignTechDialog({
  open,
  onOpenChange,
  selectedOrderIds,
  onSuccess,
}: AssignTechDialogProps) {
  const { data: technicians = [] } = useTechnicians()
  const queryClient = useQueryClient()
  const [selectedTech, setSelectedTech] = useState<string>('')

  const mutation = useMutation({
    mutationFn: (techId: string) => assignTechToOrders(selectedOrderIds, techId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
      toast.success(
        `Assigned technician to ${selectedOrderIds.length} order${selectedOrderIds.length === 1 ? '' : 's'}`,
      )
      onSuccess()
      onOpenChange(false)
      setSelectedTech('')
    },
    onError: (err: Error) => {
      toast.error(err.message || 'Failed to assign technician')
    },
  })

  const handleSubmit = () => {
    if (!selectedTech) {
      toast.error('Please select a technician')
      return
    }
    mutation.mutate(selectedTech)
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Assign Technician</DialogTitle>
          <DialogDescription>
            Assign a technician to {selectedOrderIds.length} selected order
            {selectedOrderIds.length === 1 ? '' : 's'}
          </DialogDescription>
        </DialogHeader>

        <Select value={selectedTech} onValueChange={setSelectedTech}>
          <SelectTrigger>
            <SelectValue placeholder='Select technician' />
          </SelectTrigger>
          <SelectContent>
            {technicians.map((tech) => (
              <SelectItem key={tech.id} value={tech.id}>
                {tech.name}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>

        <DialogFooter>
          <Button variant='outline' onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button onClick={handleSubmit} disabled={mutation.isPending || !selectedTech}>
            {mutation.isPending ? 'Assigning...' : 'Assign'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
