import { Suspense, useState } from 'react'
import { useLocation, useSearch } from 'wouter'
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetDescription,
} from '@/components/ui/sheet'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { RODetailsForm } from './ro-details-form'
import { useRepairOrder, useUpdateRepairOrder, useDeleteRepairOrder } from '@/hooks/useRepairOrderDetails'
import { toast } from 'sonner'
import { Skeleton } from '@/components/ui/skeleton'
import { ErrorBoundary } from '@/components/ErrorBoundary'
import type { UpdateRepairOrderInput } from '@shared/validation'

function RODetailsLoading() {
  return (
    <div className='space-y-4 p-6'>
      <Skeleton className='h-8 w-48' />
      <Skeleton className='h-32 w-full' />
      <Skeleton className='h-32 w-full' />
      <Skeleton className='h-32 w-full' />
    </div>
  )
}

function RODetailsError() {
  return (
    <div className='p-6'>
      <div className='rounded-lg bg-red-50 p-4 text-red-800'>
        Failed to load repair order. Please try again.
      </div>
    </div>
  )
}

function RODetailsContent({ orderId, onClose }: { orderId: string; onClose: () => void }) {
  const { data: order } = useRepairOrder(orderId)
  const updateMutation = useUpdateRepairOrder()
  const deleteMutation = useDeleteRepairOrder()
  const [showDeleteDialog, setShowDeleteDialog] = useState(false)

  const handleSubmit = (data: UpdateRepairOrderInput) => {
    updateMutation.mutate(
      { id: orderId, data },
      {
        onSuccess: () => {
          toast.success('Repair order updated successfully')
          onClose()
        },
        onError: (error: Error) => {
          toast.error(error.message || 'Failed to update repair order')
        },
      },
    )
  }

  const handleDelete = () => {
    deleteMutation.mutate(orderId, {
      onSuccess: () => {
        toast.success('Repair order deleted')
        setShowDeleteDialog(false)
        onClose()
      },
      onError: (error: Error) => {
        toast.error(error.message || 'Failed to delete repair order')
      },
    })
  }

  return (
    <>
      <SheetHeader className='border-b pb-4'>
        <div className='flex items-start justify-between'>
          <div>
            <SheetTitle>Repair Order Details</SheetTitle>
            <SheetDescription className='font-mono text-sm'>{order.id}</SheetDescription>
          </div>
          <Button
            variant='outline'
            size='sm'
            onClick={() => setShowDeleteDialog(true)}
            className='text-red-600 hover:bg-red-50'
          >
            Delete
          </Button>
        </div>
      </SheetHeader>

      <RODetailsForm
        order={order}
        onSubmit={handleSubmit}
        onCancel={onClose}
        isPending={updateMutation.isPending}
      />

      {/* Delete Confirmation Dialog */}
      <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete Repair Order</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete repair order {order.id}? This action cannot be
              undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant='outline' onClick={() => setShowDeleteDialog(false)}>
              Cancel
            </Button>
            <Button
              variant='destructive'
              onClick={handleDelete}
              disabled={deleteMutation.isPending}
            >
              {deleteMutation.isPending ? 'Deleting...' : 'Delete'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  )
}

export function RODetailsDrawer() {
  const [, setLocation] = useLocation()
  const searchParams = new URLSearchParams(useSearch())
  const roId = searchParams.get('roId')

  const handleClose = () => {
    // Remove roId from URL
    searchParams.delete('roId')
    const newSearch = searchParams.toString()
    setLocation(`?${newSearch}`, { replace: true })
  }

  return (
    <Sheet open={!!roId} onOpenChange={(open) => !open && handleClose()}>
      <SheetContent side='right' className='w-full p-0 sm:max-w-2xl'>
        {roId && (
          <ErrorBoundary fallback={() => <RODetailsError />}>
            <Suspense fallback={<RODetailsLoading />}>
              <RODetailsContent orderId={roId} onClose={handleClose} />
            </Suspense>
          </ErrorBoundary>
        )}
      </SheetContent>
    </Sheet>
  )
}
