import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import type { UseFormRegister, FieldErrors } from 'react-hook-form'
import type { CreateRepairOrderInput } from '@shared/validation'

type VehicleInfoFieldsProps = {
  register: UseFormRegister<CreateRepairOrderInput>
  errors: FieldErrors<CreateRepairOrderInput>
}

export function VehicleInfoFields({ register, errors }: VehicleInfoFieldsProps) {
  const currentYear = new Date().getFullYear()

  return (
    <div className='space-y-4'>
      <h3 className='text-sm font-semibold text-gray-900'>Vehicle Information</h3>

      <div className='grid grid-cols-2 gap-4'>
        <div className='space-y-2'>
          <Label htmlFor='vehicle.year'>
            Year <span className='text-red-500'>*</span>
          </Label>
          <Input
            id='vehicle.year'
            type='number'
            {...register('vehicle.year', { valueAsNumber: true })}
            placeholder={currentYear.toString()}
            min={1900}
            max={currentYear + 1}
          />
          {errors.vehicle?.year && (
            <p className='text-xs text-red-600'>{errors.vehicle.year.message}</p>
          )}
        </div>

        <div className='space-y-2'>
          <Label htmlFor='vehicle.make'>
            Make <span className='text-red-500'>*</span>
          </Label>
          <Input id='vehicle.make' {...register('vehicle.make')} placeholder='Toyota' />
          {errors.vehicle?.make && (
            <p className='text-xs text-red-600'>{errors.vehicle.make.message}</p>
          )}
        </div>
      </div>

      <div className='grid grid-cols-2 gap-4'>
        <div className='space-y-2'>
          <Label htmlFor='vehicle.model'>
            Model <span className='text-red-500'>*</span>
          </Label>
          <Input id='vehicle.model' {...register('vehicle.model')} placeholder='Camry' />
          {errors.vehicle?.model && (
            <p className='text-xs text-red-600'>{errors.vehicle.model.message}</p>
          )}
        </div>

        <div className='space-y-2'>
          <Label htmlFor='vehicle.trim'>Trim</Label>
          <Input id='vehicle.trim' {...register('vehicle.trim')} placeholder='XLE' />
          {errors.vehicle?.trim && (
            <p className='text-xs text-red-600'>{errors.vehicle.trim.message}</p>
          )}
        </div>
      </div>

      <div className='space-y-2'>
        <Label htmlFor='vehicle.vin'>VIN</Label>
        <Input
          id='vehicle.vin'
          {...register('vehicle.vin')}
          placeholder='1HGBH41JXMN109186'
          maxLength={17}
          className='font-mono'
        />
        {errors.vehicle?.vin && (
          <p className='text-xs text-red-600'>{errors.vehicle.vin.message}</p>
        )}
      </div>

      <div className='grid grid-cols-2 gap-4'>
        <div className='space-y-2'>
          <Label htmlFor='vehicle.plate'>License Plate</Label>
          <Input
            id='vehicle.plate'
            {...register('vehicle.plate')}
            placeholder='ABC-1234'
          />
          {errors.vehicle?.plate && (
            <p className='text-xs text-red-600'>{errors.vehicle.plate.message}</p>
          )}
        </div>

        <div className='space-y-2'>
          <Label htmlFor='vehicle.mileage'>Mileage</Label>
          <Input
            id='vehicle.mileage'
            type='number'
            {...register('vehicle.mileage', { valueAsNumber: true })}
            placeholder='50000'
            min={0}
          />
          {errors.vehicle?.mileage && (
            <p className='text-xs text-red-600'>{errors.vehicle.mileage.message}</p>
          )}
        </div>
      </div>

      <div className='space-y-2'>
        <Label htmlFor='vehicle.color'>Color</Label>
        <Input id='vehicle.color' {...register('vehicle.color')} placeholder='Silver' />
        {errors.vehicle?.color && (
          <p className='text-xs text-red-600'>{errors.vehicle.color.message}</p>
        )}
      </div>
    </div>
  )
}
