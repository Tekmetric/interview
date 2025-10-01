import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Checkbox } from '@/components/ui/checkbox'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import { useTechnicians } from '@/hooks/useTechnicians'
import { canTransition, ALLOWED_TRANSITIONS } from '@shared/transitions'
import type { RepairOrder, RepairOrderStatus, Priority } from '@shared/types'
import { updateRepairOrderSchema } from '@shared/validation'

type RODetailsFormProps = {
  order: RepairOrder
  onSubmit: (data: z.infer<typeof updateRepairOrderSchema>) => void
  onCancel: () => void
  isPending?: boolean
}

export function RODetailsForm({ order, onSubmit, onCancel, isPending }: RODetailsFormProps) {
  const { data: technicians } = useTechnicians()

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    setValue,
  } = useForm<z.infer<typeof updateRepairOrderSchema>>({
    resolver: zodResolver(updateRepairOrderSchema),
    defaultValues: {
      status: order.status,
      assignedTech: order.assignedTech ? { id: order.assignedTech.id } : null,
      priority: order.priority,
      notes: order.notes,
      approvedByCustomer: order.approvedByCustomer,
    },
  })

  const currentStatus = watch('status')
  const approvedByCustomer = watch('approvedByCustomer')

  // Get allowed status transitions
  const allowedStatuses = ALLOWED_TRANSITIONS[order.status] || []
  const watchedTechId = watch('assignedTech')?.id

  // Filter status options based on validation
  const getStatusOptions = () => {
    const options: RepairOrderStatus[] = [order.status]
    allowedStatuses.forEach((status) => {
      const assignedTech = watchedTechId
        ? technicians.find((t) => t.id === watchedTechId)
        : null
      const validation = canTransition(order.status, status, {
        ...order,
        assignedTech,
        approvedByCustomer,
      })
      if (validation.allowed) {
        options.push(status)
      }
    })
    return options
  }

  const statusOptions = getStatusOptions()

  const statusColors: Record<RepairOrderStatus, string> = {
    NEW: 'bg-blue-500',
    AWAITING_APPROVAL: 'bg-amber-500',
    IN_PROGRESS: 'bg-indigo-500',
    WAITING_PARTS: 'bg-orange-500',
    COMPLETED: 'bg-green-500',
  }

  const priorityColors: Record<Priority, string> = {
    HIGH: 'border-red-500 text-red-700',
    NORMAL: 'border-gray-300 text-gray-600',
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className='flex h-full flex-col'>
      <div className='flex-1 space-y-6 overflow-y-auto px-6 py-4'>
        {/* Order Info - Read Only */}
        <div className='space-y-3 rounded-lg border bg-gray-50 p-4'>
          <h3 className='font-semibold text-gray-900'>Order Information</h3>
          <div className='grid grid-cols-2 gap-3 text-sm'>
            <div>
              <span className='text-gray-500'>Order ID:</span>
              <p className='font-mono font-semibold'>{order.id}</p>
            </div>
            <div>
              <span className='text-gray-500'>Created:</span>
              <p>{new Date(order.createdAt).toLocaleDateString()}</p>
            </div>
            <div>
              <span className='text-gray-500'>Last Updated:</span>
              <p>{new Date(order.updatedAt).toLocaleDateString()}</p>
            </div>
          </div>
        </div>

        {/* Customer Info - Read Only */}
        <div className='space-y-3 rounded-lg border bg-gray-50 p-4'>
          <h3 className='font-semibold text-gray-900'>Customer</h3>
          <div className='space-y-2 text-sm'>
            <div>
              <span className='text-gray-500'>Name:</span>
              <p className='font-medium'>{order.customer.name}</p>
            </div>
            <div>
              <span className='text-gray-500'>Phone:</span>
              <p>{order.customer.phone}</p>
            </div>
            {order.customer.email && (
              <div>
                <span className='text-gray-500'>Email:</span>
                <p>{order.customer.email}</p>
              </div>
            )}
          </div>
        </div>

        {/* Vehicle Info - Read Only */}
        <div className='space-y-3 rounded-lg border bg-gray-50 p-4'>
          <h3 className='font-semibold text-gray-900'>Vehicle</h3>
          <div className='space-y-2 text-sm'>
            <div>
              <span className='text-gray-500'>Vehicle:</span>
              <p className='font-medium'>
                {order.vehicle.year} {order.vehicle.make} {order.vehicle.model}{' '}
                {order.vehicle.trim}
              </p>
            </div>
            {order.vehicle.vin && (
              <div>
                <span className='text-gray-500'>VIN:</span>
                <p className='font-mono text-xs'>{order.vehicle.vin}</p>
              </div>
            )}
            {order.vehicle.plate && (
              <div>
                <span className='text-gray-500'>Plate:</span>
                <p>{order.vehicle.plate}</p>
              </div>
            )}
            {order.vehicle.mileage && (
              <div>
                <span className='text-gray-500'>Mileage:</span>
                <p>{order.vehicle.mileage.toLocaleString()} mi</p>
              </div>
            )}
            {order.vehicle.color && (
              <div>
                <span className='text-gray-500'>Color:</span>
                <p>{order.vehicle.color}</p>
              </div>
            )}
          </div>
        </div>

        {/* Services - Read Only */}
        <div className='space-y-3'>
          <Label>Services</Label>
          <div className='flex flex-wrap gap-2'>
            {order.services.map((service, idx) => (
              <Badge key={idx} variant='outline'>
                {service}
              </Badge>
            ))}
          </div>
        </div>

        {/* Editable Fields */}
        <div className='space-y-4 border-t pt-4'>
          <h3 className='font-semibold text-gray-900'>Update Order</h3>

          {/* Status */}
          <div className='space-y-2'>
            <Label htmlFor='status'>Status</Label>
            <Select
              value={currentStatus}
              onValueChange={(value) => setValue('status', value as RepairOrderStatus)}
            >
              <SelectTrigger>
                <SelectValue>
                  <Badge className={statusColors[currentStatus || order.status]}>
                    {currentStatus || order.status}
                  </Badge>
                </SelectValue>
              </SelectTrigger>
              <SelectContent>
                {statusOptions.map((status) => (
                  <SelectItem key={status} value={status}>
                    <Badge className={statusColors[status]}>{status}</Badge>
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            {errors.status && (
              <p className='text-xs text-red-600'>{errors.status.message}</p>
            )}
          </div>

          {/* Assigned Tech */}
          <div className='space-y-2'>
            <Label htmlFor='assignedTech'>Assigned Technician</Label>
            <Select
              value={watch('assignedTech')?.id || 'none'}
              onValueChange={(value) => {
                if (value === 'none') {
                  setValue('assignedTech', null)
                } else {
                  const tech = technicians.find((t) => t.id === value)
                  if (tech) setValue('assignedTech', tech)
                }
              }}
            >
              <SelectTrigger>
                <SelectValue placeholder='Select technician' />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value='none'>Unassigned</SelectItem>
                {technicians
                  .filter((t) => t.active)
                  .map((tech) => (
                    <SelectItem key={tech.id} value={tech.id}>
                      {tech.name}
                    </SelectItem>
                  ))}
              </SelectContent>
            </Select>
            {errors.assignedTech && (
              <p className='text-xs text-red-600'>{errors.assignedTech.message}</p>
            )}
          </div>

          {/* Priority */}
          <div className='space-y-2'>
            <Label htmlFor='priority'>Priority</Label>
            <Select
              value={watch('priority')}
              onValueChange={(value) => setValue('priority', value as Priority)}
            >
              <SelectTrigger>
                <SelectValue>
                  <Badge
                    variant='outline'
                    className={priorityColors[watch('priority') || 'NORMAL']}
                  >
                    {watch('priority') || 'NORMAL'}
                  </Badge>
                </SelectValue>
              </SelectTrigger>
              <SelectContent>
                <SelectItem value='NORMAL'>
                  <Badge variant='outline' className={priorityColors.NORMAL}>
                    NORMAL
                  </Badge>
                </SelectItem>
                <SelectItem value='HIGH'>
                  <Badge variant='outline' className={priorityColors.HIGH}>
                    HIGH
                  </Badge>
                </SelectItem>
              </SelectContent>
            </Select>
            {errors.priority && (
              <p className='text-xs text-red-600'>{errors.priority.message}</p>
            )}
          </div>

          {/* Approved by Customer */}
          <div className='flex items-center gap-2'>
            <Checkbox
              id='approvedByCustomer'
              checked={approvedByCustomer}
              onCheckedChange={(checked) =>
                setValue('approvedByCustomer', checked as boolean)
              }
            />
            <Label htmlFor='approvedByCustomer' className='cursor-pointer'>
              Approved by Customer
            </Label>
          </div>

          {/* Notes */}
          <div className='space-y-2'>
            <Label htmlFor='notes'>Notes</Label>
            <Textarea
              id='notes'
              placeholder='Add notes about this repair order...'
              rows={4}
              {...register('notes')}
            />
            {errors.notes && <p className='text-xs text-red-600'>{errors.notes.message}</p>}
          </div>

          {/* Additional Info - Read Only */}
          {(order.estimatedDuration || order.estimatedCost || order.dueTime) && (
            <div className='space-y-2 rounded-lg border bg-gray-50 p-3'>
              <h4 className='text-sm font-semibold text-gray-700'>Estimates</h4>
              <div className='space-y-1 text-sm'>
                {order.estimatedDuration && (
                  <div>
                    <span className='text-gray-500'>Duration:</span> {order.estimatedDuration}{' '}
                    hours
                  </div>
                )}
                {order.estimatedCost && (
                  <div>
                    <span className='text-gray-500'>Cost:</span> $
                    {order.estimatedCost.toLocaleString()}
                  </div>
                )}
                {order.dueTime && (
                  <div>
                    <span className='text-gray-500'>Due:</span>{' '}
                    {new Date(order.dueTime).toLocaleString()}
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Form Actions */}
      <div className='flex gap-3 border-t bg-gray-50 px-6 py-4'>
        <Button type='button' variant='outline' onClick={onCancel} disabled={isPending}>
          Cancel
        </Button>
        <Button type='submit' disabled={isPending} className='flex-1'>
          {isPending ? 'Saving...' : 'Save Changes'}
        </Button>
      </div>
    </form>
  )
}
