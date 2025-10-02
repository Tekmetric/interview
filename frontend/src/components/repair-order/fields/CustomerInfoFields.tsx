import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import type { UseFormRegister, FieldErrors } from 'react-hook-form'
import type { CreateRepairOrderInput } from '@shared/validation'
import type { ReactNode } from 'react'

type CustomerInfoFieldsProps = {
  register: UseFormRegister<CreateRepairOrderInput>
  errors: FieldErrors<CreateRepairOrderInput>
  headerAction?: ReactNode
}

export function CustomerInfoFields({
  register,
  errors,
  headerAction,
}: CustomerInfoFieldsProps) {
  return (
    <div className='space-y-4'>
      <div className='flex items-center justify-between'>
        <h3 className='text-sm font-semibold text-gray-900'>Customer Information</h3>
        {headerAction}
      </div>

      <div className='space-y-2'>
        <Label htmlFor='customer.name'>
          Name <span className='text-red-500'>*</span>
        </Label>
        <Input id='customer.name' {...register('customer.name')} placeholder='John Doe' />
        {errors.customer?.name && (
          <p className='text-xs text-red-600'>{errors.customer.name.message}</p>
        )}
      </div>

      <div className='space-y-2'>
        <Label htmlFor='customer.phone'>
          Phone <span className='text-red-500'>*</span>
        </Label>
        <Input
          id='customer.phone'
          type='tel'
          {...register('customer.phone')}
          placeholder='(555) 123-4567'
        />
        {errors.customer?.phone && (
          <p className='text-xs text-red-600'>{errors.customer.phone.message}</p>
        )}
      </div>

      <div className='space-y-2'>
        <Label htmlFor='customer.email'>Email</Label>
        <Input
          id='customer.email'
          type='email'
          {...register('customer.email')}
          placeholder='john@example.com'
        />
        {errors.customer?.email && (
          <p className='text-xs text-red-600'>{errors.customer.email.message}</p>
        )}
      </div>
    </div>
  )
}
