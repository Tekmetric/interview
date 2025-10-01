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
  onDelete: () => void
  isPending?: boolean
  isDeleting?: boolean
}

export function RODetailsForm({
  order,
  onSubmit,
  onCancel,
  onDelete,
  isPending,
  isDeleting,
}: RODetailsFormProps) {
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
    <form onSubmit={handleSubmit(onSubmit)} className='flex h-full flex-col overflow-hidden'>
      <div className='flex-1 space-y-4 overflow-y-auto p-6 pb-4'>
        {/* Order Info - Read Only */}
        <div className='space-y-2 rounded-lg border border-gray-200 bg-gray-50 p-3'>
          <h3 className='text-sm font-semibold text-gray-900'>Order Information</h3>
          <div className='grid grid-cols-2 gap-x-4 gap-y-2 text-xs'>
            <div>
              <p className='text-gray-500'>Order ID:</p>
              <p className='font-mono font-semibold text-gray-900'>{order.id}</p>
            </div>
            <div>
              <p className='text-gray-500'>Created:</p>
              <p className='text-gray-900'>{new Date(order.createdAt).toLocaleDateString()}</p>
            </div>
            <div className='col-span-2'>
              <p className='text-gray-500'>Last Updated:</p>
              <p className='text-gray-900'>{new Date(order.updatedAt).toLocaleDateString()}</p>
            </div>
          </div>
        </div>

        {/* Customer Info - Read Only */}
        <div className='space-y-2 rounded-lg border border-gray-200 bg-gray-50 p-3'>
          <h3 className='text-sm font-semibold text-gray-900'>Customer</h3>
          <div className='space-y-1.5 text-xs'>
            <div>
              <p className='text-gray-500'>Name:</p>
              <p className='font-medium text-gray-900'>{order.customer.name}</p>
            </div>
            <div>
              <p className='text-gray-500'>Phone:</p>
              <p className='text-gray-900'>{order.customer.phone}</p>
            </div>
            {order.customer.email && (
              <div>
                <p className='text-gray-500'>Email:</p>
                <p className='text-gray-900'>{order.customer.email}</p>
              </div>
            )}
          </div>
        </div>

        {/* Vehicle Info - Read Only */}
        <div className='space-y-2 rounded-lg border border-gray-200 bg-gray-50 p-3'>
          <h3 className='text-sm font-semibold text-gray-900'>Vehicle</h3>
          <div className='space-y-1.5 text-xs'>
            <div>
              <p className='text-gray-500'>Vehicle:</p>
              <p className='font-medium text-gray-900'>
                {order.vehicle.year} {order.vehicle.make} {order.vehicle.model}{' '}
                {order.vehicle.trim}
              </p>
            </div>
            {order.vehicle.vin && (
              <div>
                <p className='text-gray-500'>VIN:</p>
                <p className='font-mono text-xs text-gray-900'>{order.vehicle.vin}</p>
              </div>
            )}
            {order.vehicle.plate && (
              <div>
                <p className='text-gray-500'>Plate:</p>
                <p className='text-gray-900'>{order.vehicle.plate}</p>
              </div>
            )}
            {order.vehicle.mileage && (
              <div>
                <p className='text-gray-500'>Mileage:</p>
                <p className='text-gray-900'>{order.vehicle.mileage.toLocaleString()} mi</p>
              </div>
            )}
            {order.vehicle.color && (
              <div>
                <p className='text-gray-500'>Color:</p>
                <p className='capitalize text-gray-900'>{order.vehicle.color}</p>
              </div>
            )}
          </div>
        </div>

        {/* Services - Read Only */}
        <div className='space-y-2'>
          <Label className='text-sm font-semibold'>Services</Label>
          <div className='flex flex-wrap gap-1.5'>
            {order.services.map((service, idx) => (
              <Badge key={idx} variant='outline' className='text-xs'>
                {service}
              </Badge>
            ))}
          </div>
        </div>

        {/* Editable Fields */}
        <div className='space-y-4 border-t pt-4'>
          <h3 className='text-sm font-semibold text-gray-900'>Update Order</h3>

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
            <div className='space-y-1.5 rounded-lg border border-gray-200 bg-gray-50 p-3'>
              <h4 className='text-xs font-semibold text-gray-700'>Estimates</h4>
              <div className='space-y-1 text-xs'>
                {order.estimatedDuration && (
                  <p className='text-gray-900'>
                    <span className='text-gray-500'>Duration:</span> {order.estimatedDuration} hours
                  </p>
                )}
                {order.estimatedCost && (
                  <p className='text-gray-900'>
                    <span className='text-gray-500'>Cost:</span> $
                    {order.estimatedCost.toLocaleString()}
                  </p>
                )}
                {order.dueTime && (
                  <p className='text-gray-900'>
                    <span className='text-gray-500'>Due:</span>{' '}
                    {new Date(order.dueTime).toLocaleString()}
                  </p>
                )}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Form Actions */}
      <div className='flex shrink-0 gap-2 border-t bg-white px-6 py-3 shadow-[0_-2px_8px_rgba(0,0,0,0.08)]'>
        <Button
          type='button'
          variant='outline'
          onClick={onDelete}
          disabled={isPending || isDeleting}
          className='border-red-200 text-red-600 hover:bg-red-50 hover:text-red-700'
        >
          {isDeleting ? 'Deleting...' : 'Delete'}
        </Button>
        <Button
          type='button'
          variant='outline'
          onClick={onCancel}
          disabled={isPending || isDeleting}
          className='flex-1'
        >
          Cancel
        </Button>
        <Button type='submit' disabled={isPending || isDeleting} className='flex-1'>
          {isPending ? 'Saving...' : 'Save Changes'}
        </Button>
      </div>
    </form>
  )
}
