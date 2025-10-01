import { Button } from '@/components/ui/button'
import { useRepairOrderForm } from './hooks/useRepairOrderForm'
import { CustomerInfoFields } from './fields/CustomerInfoFields'
import { VehicleInfoFields } from './fields/VehicleInfoFields'
import { ServiceSelector } from './fields/ServiceSelector'
import { ROMetadataFields } from './fields/ROMetadataFields'
import type { CreateRepairOrderInput } from '@shared/validation'

type ROCreateFormProps = {
  onSubmit: (data: CreateRepairOrderInput) => void
  onCancel: () => void
  isPending?: boolean
}

export function ROCreateForm({ onSubmit, onCancel, isPending }: ROCreateFormProps) {
  const form = useRepairOrderForm()
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    setValue,
  } = form

  const services = watch('services')

  return (
    <form
      onSubmit={handleSubmit(onSubmit as (data: CreateRepairOrderInput) => void)}
      className='flex h-[calc(100vh-100px)] flex-col overflow-hidden'
    >
      <div className='flex-1 space-y-6 overflow-y-auto p-6 pb-4'>
        <CustomerInfoFields register={register} errors={errors} />
        <VehicleInfoFields register={register} errors={errors} />
        <ServiceSelector services={services} setValue={setValue} errors={errors} />
        <ROMetadataFields register={register} setValue={setValue} watch={watch} errors={errors} />
      </div>

      {/* Form Actions */}
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
