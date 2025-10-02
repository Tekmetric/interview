import { useEffect } from 'react'
import type { MutableRefObject } from 'react'
import { Button } from '@/components/ui/button'
import { Eraser } from 'lucide-react'
import { useRepairOrderForm, getCleanFormDefaults } from './hooks/useRepairOrderForm'
import { CustomerInfoFields } from './fields/CustomerInfoFields'
import { VehicleInfoFields } from './fields/VehicleInfoFields'
import { ServiceSelector } from './fields/ServiceSelector'
import { ROMetadataFields } from './fields/ROMetadataFields'
import type { CreateRepairOrderInput } from '@shared/validation'

type ROCreateFormProps = {
  onSubmit: (data: CreateRepairOrderInput) => void
  onCancel: () => void
  isPending?: boolean
  hasLocalStorageData?: boolean
  onClear?: () => void
  resetRef?: MutableRefObject<(() => void) | null>
}

export function ROCreateForm({
  onSubmit,
  onCancel,
  isPending,
  hasLocalStorageData,
  onClear,
  resetRef,
}: ROCreateFormProps) {
  const form = useRepairOrderForm()
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    setValue,
    reset,
  } = form

  const services = watch('services')

  useEffect(() => {
    if (resetRef) {
      resetRef.current = () => reset(getCleanFormDefaults())
    }
  }, [reset, resetRef])

  const clearButton = hasLocalStorageData && onClear && (
    <Button
      type='button'
      variant='ghost'
      size='sm'
      onClick={onClear}
      disabled={isPending}
      className='gap-2'
    >
      <Eraser className='h-4 w-4' />
      Clear Form
    </Button>
  )

  return (
    <form
      onSubmit={handleSubmit(onSubmit as (data: CreateRepairOrderInput) => void)}
      className='flex h-[calc(100vh-100px)] flex-col overflow-hidden'
    >
      <div className='flex-1 space-y-6 overflow-y-auto p-6 pb-4'>
        <CustomerInfoFields
          register={register}
          errors={errors}
          headerAction={clearButton}
        />
        <VehicleInfoFields register={register} errors={errors} />
        <ServiceSelector services={services} setValue={setValue} errors={errors} />
        <ROMetadataFields
          register={register}
          setValue={setValue}
          watch={watch}
          errors={errors}
        />
      </div>

      <div className='flex shrink-0 gap-2 border-t bg-white px-6 py-3 shadow-[0_-2px_8px_rgba(0,0,0,0.08)]'>
        <Button
          type='button'
          variant='outline'
          onClick={onCancel}
          disabled={isPending}
          className='flex-1'
        >
          Cancel
        </Button>
        <Button type='submit' disabled={isPending} className='flex-1'>
          {isPending ? 'Creating...' : 'Create Repair Order'}
        </Button>
      </div>
    </form>
  )
}
