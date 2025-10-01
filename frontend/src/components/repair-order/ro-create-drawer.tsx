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
        toast.success('Repair order created successfully')
        handleClose()
      },
      onError: (error: Error) => {
        toast.error(error.message || 'Failed to create repair order')
      },
    })
  }

  return (
    <Sheet open={isOpen} onOpenChange={(open) => !open && handleClose()}>
      <SheetContent side='right' className='w-full p-0 sm:max-w-2xl'>
        <SheetHeader className='border-b bg-white px-6 py-4'>
          <SheetTitle className='text-lg'>Create New Repair Order</SheetTitle>
          <SheetDescription className='mt-1 text-sm'>
            Enter customer, vehicle, and service details
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
