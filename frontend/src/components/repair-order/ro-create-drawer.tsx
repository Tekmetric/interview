import { useLocation, useSearch } from 'wouter'
import { useState, useRef, useEffect } from 'react'
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetDescription,
} from '@/components/ui/sheet'
import { ROCreateForm } from './ro-create-form'
import { useCreateRepairOrder } from './hooks/useRepairOrderDetails'
import { clearRODraft } from './hooks/useRepairOrderForm'
import { getItem } from '@/lib/storage'
import { toast } from 'sonner'
import type { CreateRepairOrderInput } from '@shared/validation'
import { REPAIR_ORDER_LABELS } from '@shared/constants'

const STORAGE_KEY = 'ro-create-draft'

export function ROCreateDrawer() {
  const [, setLocation] = useLocation()
  const searchParams = new URLSearchParams(useSearch())
  const isOpen = searchParams.get('createRO') === 'true'
  const createMutation = useCreateRepairOrder()
  const [hasLocalStorageData, setHasLocalStorageData] = useState(false)
  const formResetRef = useRef<(() => void) | null>(null)

  useEffect(() => {
    if (isOpen) {
      const draft = getItem(STORAGE_KEY, {})
      setHasLocalStorageData(Object.keys(draft).length > 0)
    }
  }, [isOpen])

  const handleClose = () => {
    searchParams.delete('createRO')
    const newSearch = searchParams.toString()
    setLocation(`?${newSearch}`, { replace: true })
  }

  const handleClear = () => {
    clearRODraft()
    formResetRef.current?.()
    setHasLocalStorageData(false)
    toast.success('Form cleared')
  }

  const handleSubmit = (data: CreateRepairOrderInput) => {
    createMutation.mutate(data, {
      onSuccess: () => {
        clearRODraft()
        formResetRef.current?.()
        setHasLocalStorageData(false)
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
          hasLocalStorageData={hasLocalStorageData}
          onClear={handleClear}
          resetRef={formResetRef}
        />
      </SheetContent>
    </Sheet>
  )
}
