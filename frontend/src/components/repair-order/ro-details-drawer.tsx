import { Suspense, useState } from 'react'
import { useLocation, useSearch } from 'wouter'
import { Sheet, SheetContent, SheetHeader, SheetTitle } from '@/components/ui/sheet'
import { Badge } from '@/components/ui/badge'
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
import {
  useRepairOrder,
  useUpdateRepairOrder,
  useDeleteRepairOrder,
} from './hooks/useRepairOrderDetails'
import { STATUS_CONFIG } from './ro-constants'
import { toast } from 'sonner'
import { Skeleton } from '@/components/ui/skeleton'
import { ErrorBoundary } from '@/components/ErrorBoundary'
import { updateRepairOrderSchema } from '@shared/validation'
import type { z } from 'zod'
import { REPAIR_ORDER_LABELS, COMMON_LABELS } from '@shared/constants'

function RODetailsLoading() {
  return (
    <div className='space-y-4 p-6' data-testid='ro-details-loading'>
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
        {REPAIR_ORDER_LABELS.FAILED_TO_LOAD}
      </div>
    </div>
  )
}

function RODetailsContent({
  orderId,
  onClose,
}: {
  orderId: string
  onClose: () => void
}) {
  const { data: order } = useRepairOrder(orderId)
  const updateMutation = useUpdateRepairOrder()
  const deleteMutation = useDeleteRepairOrder()
  const [showDeleteDialog, setShowDeleteDialog] = useState(false)

  const handleSubmit = (data: z.input<typeof updateRepairOrderSchema>, wasInEditMode: boolean) => {
    // Parse and transform the data using the schema
    const parsedData = updateRepairOrderSchema.parse(data)

    updateMutation.mutate(
      { id: orderId, data: parsedData },
      {
        onSuccess: () => {
          toast.success(REPAIR_ORDER_LABELS.UPDATED_SUCCESS)
          // Only close drawer if we were in quick actions mode (not edit mode)
          if (!wasInEditMode) {
            onClose()
          }
        },
        onError: (error: Error) => {
          toast.error(error.message || REPAIR_ORDER_LABELS.FAILED_TO_UPDATE)
        },
      },
    )
  }

  const handleDelete = () => {
    deleteMutation.mutate(orderId, {
      onSuccess: () => {
        toast.success(REPAIR_ORDER_LABELS.DELETED_SUCCESS)
        setShowDeleteDialog(false)
        onClose()
      },
      onError: (error: Error) => {
        toast.error(error.message || REPAIR_ORDER_LABELS.FAILED_TO_DELETE)
      },
    })
  }

  const statusConfig = STATUS_CONFIG[order.status]

  return (
    <>
      <SheetHeader className='border-b bg-white px-6 py-4'>
        <div className='flex items-center gap-3'>
          <SheetTitle className='text-2xl font-bold text-gray-900'>{order.id}</SheetTitle>
          <Badge
            variant='outline'
            className={`${statusConfig.bg} ${statusConfig.text} ${statusConfig.border} h-5 border px-2 text-xs font-semibold`}
          >
            {statusConfig.label}
          </Badge>
        </div>
      </SheetHeader>

      <RODetailsForm
        order={order}
        onSubmit={handleSubmit}
        onCancel={onClose}
        onDelete={() => setShowDeleteDialog(true)}
        isPending={updateMutation.isPending}
        isDeleting={deleteMutation.isPending}
      />

      <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{REPAIR_ORDER_LABELS.DELETE_TITLE}</DialogTitle>
            <DialogDescription>
              {REPAIR_ORDER_LABELS.DELETE_CONFIRMATION.replace('{order.id}', order.id)}
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant='outline' onClick={() => setShowDeleteDialog(false)}>
              {COMMON_LABELS.CANCEL}
            </Button>
            <Button
              variant='destructive'
              onClick={handleDelete}
              disabled={deleteMutation.isPending}
            >
              {deleteMutation.isPending ? COMMON_LABELS.DELETING : COMMON_LABELS.DELETE}
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
