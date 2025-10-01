import { useLocation, useSearch } from 'wouter'
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetDescription,
} from '@/components/ui/sheet'
import { ROCreateForm } from './ro-create-form'
import { useCreateRepairOrder } from './hooks/useRepairOrderDetails'
import { toast } from 'sonner'
import type { CreateRepairOrderInput } from '@shared/validation'
import { REPAIR_ORDER_LABELS } from '@shared/constants'

export function ROCreateDrawer() {
  const [, setLocation] = useLocation()
  const searchParams = new URLSearchParams(useSearch())
  const isOpen = searchParams.get('createRO') === 'true'
  const createMutation = useCreateRepairOrder()

  const handleClose = () => {
    searchParams.delete('createRO')
    const newSearch = searchParams.toString()
    setLocation(`?${newSearch}`, { replace: true })
  }

  const handleSubmit = (data: CreateRepairOrderInput) => {
    createMutation.mutate(data, {
      onSuccess: () => {
        toast.success(REPAIR_ORDER_LABELS.CREATED_SUCCESS)
        handleClose()
      },
      onError: (error: Error) => {
        toast.error(error.message || REPAIR_ORDER_LABELS.FAILED_TO_CREATE)
      },
    })
  }

  return (
    <Sheet open={isOpen} onOpenChange={(open) => !open && handleClose()}>
      <SheetContent side='right' className='w-full p-0 sm:max-w-2xl'>
        <SheetHeader className='border-b bg-white px-6 py-4'>
          <SheetTitle className='text-lg'>{REPAIR_ORDER_LABELS.CREATE_NEW}</SheetTitle>
          <SheetDescription className='mt-1 text-sm'>
            {REPAIR_ORDER_LABELS.ENTER_DETAILS}
          </SheetDescription>
        </SheetHeader>

        <ROCreateForm
          onSubmit={handleSubmit}
          onCancel={handleClose}
          isPending={createMutation.isPending}
        />
      </SheetContent>
    </Sheet>
  )
}
