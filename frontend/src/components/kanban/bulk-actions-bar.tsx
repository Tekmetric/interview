import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { useMultiSelect } from '@/hooks/use-multi-select'
import { X, UserPlus, ArrowRight, Flag, Trash2 } from 'lucide-react'
import { AssignTechDialog } from './batch-dialogs/assign-tech-dialog'
import { ChangeStatusDialog } from './batch-dialogs/change-status-dialog'
import { UpdatePriorityDialog } from './batch-dialogs/update-priority-dialog'
import { DeleteOrdersDialog } from './batch-dialogs/delete-orders-dialog'

type BulkActionsBarProps = {
  orders: Array<{ id: string }>
}

export function BulkActionsBar({ orders }: BulkActionsBarProps) {
  const { selection, clearSelection } = useMultiSelect()
  const [assignTechOpen, setAssignTechOpen] = useState(false)
  const [changeStatusOpen, setChangeStatusOpen] = useState(false)
  const [updatePriorityOpen, setUpdatePriorityOpen] = useState(false)
  const [deleteOrdersOpen, setDeleteOrdersOpen] = useState(false)

  const selectedCount = selection.selectedIds.size
  const selectedOrderIds = Array.from(selection.selectedIds)

  if (!selection.isSelecting || selectedCount === 0) {
    return null
  }

  return (
    <>
      <div className='fixed right-1/2 bottom-4 z-50 translate-x-1/2 rounded-lg border border-gray-200 bg-white p-4 shadow-lg'>
        <div className='flex items-center gap-4'>
          <div className='flex items-center gap-2'>
            <span className='text-sm font-medium text-gray-900'>
              {selectedCount} {selectedCount === 1 ? 'order' : 'orders'} selected
            </span>
            <Button
              variant='ghost'
              size='sm'
              onClick={clearSelection}
              className='h-6 w-6 p-0'
            >
              <X className='h-4 w-4' />
            </Button>
          </div>

          <div className='h-6 w-px bg-gray-300' />

          <div className='flex gap-2'>
            <Button
              variant='outline'
              size='sm'
              onClick={() => setAssignTechOpen(true)}
              className='gap-2'
            >
              <UserPlus className='h-4 w-4' />
              Assign Tech
            </Button>

            <Button
              variant='outline'
              size='sm'
              onClick={() => setChangeStatusOpen(true)}
              className='gap-2'
            >
              <ArrowRight className='h-4 w-4' />
              Change Status
            </Button>

            <Button
              variant='outline'
              size='sm'
              onClick={() => setUpdatePriorityOpen(true)}
              className='gap-2'
            >
              <Flag className='h-4 w-4' />
              Update Priority
            </Button>

            <Button
              variant='outline'
              size='sm'
              onClick={() => setDeleteOrdersOpen(true)}
              className='gap-2 text-red-600 hover:bg-red-50 hover:text-red-700'
            >
              <Trash2 className='h-4 w-4' />
              Delete
            </Button>
          </div>
        </div>
      </div>

      <AssignTechDialog
        open={assignTechOpen}
        onOpenChange={setAssignTechOpen}
        selectedOrderIds={selectedOrderIds}
        onSuccess={clearSelection}
      />

      <ChangeStatusDialog
        open={changeStatusOpen}
        onOpenChange={setChangeStatusOpen}
        selectedOrderIds={selectedOrderIds}
        onSuccess={clearSelection}
      />

      <UpdatePriorityDialog
        open={updatePriorityOpen}
        onOpenChange={setUpdatePriorityOpen}
        selectedOrderIds={selectedOrderIds}
        onSuccess={clearSelection}
      />

      <DeleteOrdersDialog
        open={deleteOrdersOpen}
        onOpenChange={setDeleteOrdersOpen}
        selectedOrderIds={selectedOrderIds}
        onSuccess={clearSelection}
      />
    </>
  )
}
