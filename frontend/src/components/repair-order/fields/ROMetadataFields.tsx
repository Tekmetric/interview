import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import { PRIORITY_COLORS } from '../ro-constants'
import type { UseFormRegister, UseFormSetValue, UseFormWatch, FieldErrors } from 'react-hook-form'
import type { CreateRepairOrderInput } from '@shared/validation'
import type { Priority } from '@shared/types'

type ROMetadataFieldsProps = {
  register: UseFormRegister<CreateRepairOrderInput>
  setValue: UseFormSetValue<CreateRepairOrderInput>
  watch: UseFormWatch<CreateRepairOrderInput>
  errors: FieldErrors<CreateRepairOrderInput>
}

export function ROMetadataFields({ register, setValue, watch, errors }: ROMetadataFieldsProps) {
  const priority = watch('priority')

  return (
    <div className='space-y-4'>
      <h3 className='text-sm font-semibold text-gray-900'>Additional Details</h3>

      <div className='space-y-2'>
        <Label htmlFor='priority'>Priority</Label>
        <Select
          value={priority}
          onValueChange={(value) => setValue('priority', value as Priority)}
        >
          <SelectTrigger>
            <SelectValue>
              <Badge variant='outline' className={PRIORITY_COLORS[priority || 'NORMAL']}>
                {priority || 'NORMAL'}
              </Badge>
            </SelectValue>
          </SelectTrigger>
          <SelectContent>
            <SelectItem value='NORMAL'>
              <Badge variant='outline' className={PRIORITY_COLORS.NORMAL}>
                NORMAL
              </Badge>
            </SelectItem>
            <SelectItem value='HIGH'>
              <Badge variant='outline' className={PRIORITY_COLORS.HIGH}>
                HIGH
              </Badge>
            </SelectItem>
          </SelectContent>
        </Select>
        {errors.priority && (
          <p className='text-xs text-red-600'>{errors.priority.message}</p>
        )}
      </div>

      <div className='grid grid-cols-2 gap-4'>
        <div className='space-y-2'>
          <Label htmlFor='estimatedDuration'>Estimated Duration (hours)</Label>
          <Input
            id='estimatedDuration'
            type='number'
            {...register('estimatedDuration', { valueAsNumber: true })}
            placeholder='2'
            min={0}
          />
          {errors.estimatedDuration && (
            <p className='text-xs text-red-600'>{errors.estimatedDuration.message}</p>
          )}
        </div>

        <div className='space-y-2'>
          <Label htmlFor='estimatedCost'>Estimated Cost ($)</Label>
          <Input
            id='estimatedCost'
            type='number'
            {...register('estimatedCost', { valueAsNumber: true })}
            placeholder='150'
            min={0}
          />
          {errors.estimatedCost && (
            <p className='text-xs text-red-600'>{errors.estimatedCost.message}</p>
          )}
        </div>
      </div>

      <div className='space-y-2'>
        <Label htmlFor='dueTime'>Due Date & Time</Label>
        <Input
          id='dueTime'
          type='datetime-local'
          {...register('dueTime')}
        />
        {errors.dueTime && (
          <p className='text-xs text-red-600'>{errors.dueTime.message}</p>
        )}
      </div>

      <div className='space-y-2'>
        <Label htmlFor='notes'>Notes</Label>
        <Textarea
          id='notes'
          placeholder='Add notes about this repair order...'
          rows={4}
          {...register('notes')}
        />
        {errors.notes && (
          <p className='text-xs text-red-600'>{errors.notes.message}</p>
        )}
      </div>
    </div>
  )
}
