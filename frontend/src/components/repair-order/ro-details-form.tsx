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
import { useTechnicians } from '@/components/technician/hooks/useTechnicians'
import { canTransition, ALLOWED_TRANSITIONS } from '@shared/transitions'
import type { RepairOrder, RepairOrderStatus, Priority } from '@shared/types'
import { updateRepairOrderSchema } from '@shared/validation'
import { STATUS_COLORS, PRIORITY_COLORS } from './ro-constants'
import { REPAIR_ORDER_LABELS, COMMON_LABELS } from '@shared/constants'

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

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className='flex h-[calc(100vh-100px)] flex-col overflow-hidden'
    >
      <div className='flex-1 space-y-6 overflow-y-auto p-6 pb-4'>
        {/* Order Info - Read Only */}
        <div className='space-y-3 rounded-lg border border-gray-200 bg-gray-50 p-4'>
          <h3 className='font-semibold text-gray-900'>{REPAIR_ORDER_LABELS.ORDER_INFORMATION}</h3>
          <div className='grid grid-cols-2 gap-x-6 gap-y-2 text-sm'>
            <div>
              <p className='text-gray-500'>{REPAIR_ORDER_LABELS.ORDER_ID}</p>
              <p className='font-mono font-semibold text-gray-900'>{order.id}</p>
            </div>
            <div>
              <p className='text-gray-500'>{REPAIR_ORDER_LABELS.CREATED}</p>
              <p className='text-gray-900'>
                {new Date(order.createdAt).toLocaleDateString()}
              </p>
            </div>
            <div className='col-span-2'>
              <p className='text-gray-500'>{REPAIR_ORDER_LABELS.LAST_UPDATED}</p>
              <p className='text-gray-900'>
                {new Date(order.updatedAt).toLocaleDateString()}
              </p>
            </div>
          </div>
        </div>

        {/* Customer & Vehicle - Side by Side */}
        <div className='grid grid-cols-2 gap-4'>
          {/* Customer Info */}
          <div className='space-y-3 rounded-lg border border-gray-200 bg-gray-50 p-4'>
            <h3 className='font-semibold text-gray-900'>{REPAIR_ORDER_LABELS.CUSTOMER}</h3>
            <div className='space-y-2 text-sm'>
              <p className='font-medium text-gray-900'>{order.customer.name}</p>
              <p className='text-gray-700'>{order.customer.phone}</p>
              {order.customer.email && (
                <p className='text-gray-700'>{order.customer.email}</p>
              )}
            </div>
          </div>

          {/* Vehicle Info */}
          <div className='space-y-3 rounded-lg border border-gray-200 bg-gray-50 p-4'>
            <h3 className='font-semibold text-gray-900'>{REPAIR_ORDER_LABELS.VEHICLE}</h3>
            <div className='space-y-2 text-sm'>
              <p className='font-medium text-gray-900'>
                {order.vehicle.year} {order.vehicle.make} {order.vehicle.model}
              </p>
              {order.vehicle.vin && (
                <p className='font-mono text-xs text-gray-700'>{order.vehicle.vin}</p>
              )}
              <div className='flex gap-4 text-xs'>
                {order.vehicle.plate && (
                  <span className='text-gray-700'>{REPAIR_ORDER_LABELS.PLATE} {order.vehicle.plate}</span>
                )}
                {order.vehicle.mileage && (
                  <span className='text-gray-700'>
                    {order.vehicle.mileage.toLocaleString()} {REPAIR_ORDER_LABELS.MI}
                  </span>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Services - More Prominent */}
        <div className='space-y-3 rounded-lg border-2 border-blue-200 bg-blue-50 p-4'>
          <h3 className='font-semibold text-gray-900'>{REPAIR_ORDER_LABELS.SERVICES}</h3>
          <div className='flex flex-wrap gap-2'>
            {order.services.map((service, idx) => (
              <Badge
                key={idx}
                variant='default'
                className='bg-blue-600 px-3 py-1 text-sm hover:bg-blue-700'
              >
                {service}
              </Badge>
            ))}
          </div>
        </div>

        {/* Editable Fields */}
        <div className='space-y-4 border-t pt-4'>
          <h3 className='text-sm font-semibold text-gray-900'>{REPAIR_ORDER_LABELS.UPDATE_ORDER}</h3>

          {/* Status */}
          <div className='space-y-2'>
            <Label htmlFor='status'>{REPAIR_ORDER_LABELS.STATUS}</Label>
            <Select
              value={currentStatus}
              onValueChange={(value) => setValue('status', value as RepairOrderStatus)}
            >
              <SelectTrigger>
                <SelectValue>
                  <Badge className={STATUS_COLORS[currentStatus as RepairOrderStatus || order.status]}>
                    {currentStatus || order.status}
                  </Badge>
                </SelectValue>
              </SelectTrigger>
              <SelectContent>
                {statusOptions.map((status) => (
                  <SelectItem key={status} value={status}>
                    <Badge className={STATUS_COLORS[status]}>{status}</Badge>
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
            <Label htmlFor='assignedTech'>{REPAIR_ORDER_LABELS.ASSIGNED_TECHNICIAN}</Label>
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
                <SelectValue placeholder={REPAIR_ORDER_LABELS.SELECT_TECHNICIAN} />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value='none'>{COMMON_LABELS.UNASSIGNED}</SelectItem>
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
            <Label htmlFor='priority'>{REPAIR_ORDER_LABELS.PRIORITY}</Label>
            <Select
              value={watch('priority')}
              onValueChange={(value) => setValue('priority', value as Priority)}
            >
              <SelectTrigger>
                <SelectValue>
                  <Badge
                    variant='outline'
                    className={PRIORITY_COLORS[watch('priority') as Priority || 'NORMAL']}
                  >
                    {watch('priority') || 'NORMAL'}
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
              {REPAIR_ORDER_LABELS.APPROVED_BY_CUSTOMER}
            </Label>
          </div>

          {/* Notes */}
          <div className='space-y-2'>
            <Label htmlFor='notes'>{REPAIR_ORDER_LABELS.NOTES}</Label>
            <Textarea
              id='notes'
              placeholder={REPAIR_ORDER_LABELS.ADD_NOTES_PLACEHOLDER}
              rows={4}
              {...register('notes')}
            />
            {errors.notes && (
              <p className='text-xs text-red-600'>{errors.notes.message}</p>
            )}
          </div>

          {/* Additional Info - Read Only */}
          {(order.estimatedDuration || order.estimatedCost || order.dueTime) && (
            <div className='space-y-1.5 rounded-lg border border-gray-200 bg-gray-50 p-3'>
              <h4 className='text-xs font-semibold text-gray-700'>{REPAIR_ORDER_LABELS.ESTIMATES}</h4>
              <div className='space-y-1 text-xs'>
                {order.estimatedDuration && (
                  <p className='text-gray-900'>
                    <span className='text-gray-500'>{REPAIR_ORDER_LABELS.DURATION}</span>{' '}
                    {order.estimatedDuration} {REPAIR_ORDER_LABELS.HOURS}
                  </p>
                )}
                {order.estimatedCost && (
                  <p className='text-gray-900'>
                    <span className='text-gray-500'>{REPAIR_ORDER_LABELS.COST}</span> $
                    {order.estimatedCost.toLocaleString()}
                  </p>
                )}
                {order.dueTime && (
                  <p className='text-gray-900'>
                    <span className='text-gray-500'>{REPAIR_ORDER_LABELS.DUE}</span>{' '}
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
          {isDeleting ? COMMON_LABELS.DELETING : COMMON_LABELS.DELETE}
        </Button>
        <Button
          type='button'
          variant='outline'
          onClick={onCancel}
          disabled={isPending || isDeleting}
          className='flex-1'
        >
          {COMMON_LABELS.CANCEL}
        </Button>
        <Button type='submit' disabled={isPending || isDeleting} className='flex-1'>
          {isPending ? COMMON_LABELS.SAVING : COMMON_LABELS.SAVE_CHANGES}
        </Button>
      </div>
    </form>
  )
}
