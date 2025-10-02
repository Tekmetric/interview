import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'
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
import {
  User,
  Phone,
  Mail,
  Car,
  Hash,
  CreditCard,
  Gauge,
  CheckCircle,
  Play,
  Send,
} from 'lucide-react'
import { ROTimeline } from './ro-timeline'
import { useTechnicians } from '@/components/technician/hooks/useTechnicians'
import { canTransition, ALLOWED_TRANSITIONS } from '@shared/transitions'
import type { RepairOrder, RepairOrderStatus, Priority } from '@shared/types'
import { updateRepairOrderSchema } from '@shared/validation'
import { STATUS_COLORS, PRIORITY_COLORS } from './ro-constants'
import { REPAIR_ORDER_LABELS, COMMON_LABELS, RO_STATUS } from '@shared/constants'

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

  // Determine contextual button text and icon based on status change
  const getButtonConfig = () => {
    if (currentStatus === order.status) {
      return { text: COMMON_LABELS.SAVE_CHANGES, icon: null }
    }

    switch (currentStatus) {
      case RO_STATUS.COMPLETED:
        return { text: 'Finish Repair Order', icon: <CheckCircle className='h-4 w-4' /> }
      case RO_STATUS.IN_PROGRESS:
        return { text: 'Start Work', icon: <Play className='h-4 w-4' /> }
      case RO_STATUS.AWAITING_APPROVAL:
        return { text: 'Submit for Approval', icon: <Send className='h-4 w-4' /> }
      default:
        return { text: COMMON_LABELS.SAVE_CHANGES, icon: null }
    }
  }

  const buttonConfig = getButtonConfig()

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
      className='flex h-[calc(100vh-70px)] flex-col overflow-hidden'
    >
      <div className='flex flex-1 flex-col gap-6 overflow-y-auto px-6 py-4'>
        <div className='flex flex-col gap-6'>
          <div className='grid grid-cols-1 gap-4 md:grid-cols-2'>
            <div className='flex flex-col gap-3 rounded-lg bg-gray-50 p-3'>
              <div className='flex items-center gap-2'>
                <User className='h-4 w-4 text-gray-500' />
                <h3 className='text-xs font-semibold text-gray-500 uppercase'>
                  {REPAIR_ORDER_LABELS.CUSTOMER}
                </h3>
              </div>
              <div className='flex flex-col gap-2 text-sm'>
                <p className='font-semibold text-gray-900'>{order.customer.name}</p>
                <div className='flex items-center gap-2 text-gray-700'>
                  <Phone className='h-3 w-3' />
                  <span>{order.customer.phone}</span>
                </div>
                {order.customer.email && (
                  <div className='flex items-center gap-2 text-gray-700'>
                    <Mail className='h-3 w-3' />
                    <span>{order.customer.email}</span>
                  </div>
                )}
              </div>
            </div>

            <div className='flex flex-col gap-3 rounded-lg bg-gray-50 p-3'>
              <div className='flex items-center gap-2'>
                <Car className='h-4 w-4 text-gray-500' />
                <h3 className='text-xs font-semibold text-gray-500 uppercase'>
                  {REPAIR_ORDER_LABELS.VEHICLE}
                </h3>
              </div>
              <div className='flex flex-col gap-2 text-sm'>
                <p className='font-semibold text-gray-900'>
                  {order.vehicle.year} {order.vehicle.make} {order.vehicle.model}
                </p>
                {order.vehicle.vin && (
                  <div className='flex items-center gap-2 text-gray-700'>
                    <Hash className='h-3 w-3' />
                    <span className='font-mono text-xs'>{order.vehicle.vin}</span>
                  </div>
                )}
                <div className='flex gap-3 text-xs text-gray-600'>
                  {order.vehicle.plate && (
                    <div className='flex items-center gap-1'>
                      <CreditCard className='h-3 w-3' />
                      <span>{order.vehicle.plate}</span>
                    </div>
                  )}
                  {order.vehicle.mileage && (
                    <div className='flex items-center gap-1'>
                      <Gauge className='h-3 w-3' />
                      <span>
                        {order.vehicle.mileage.toLocaleString()} {REPAIR_ORDER_LABELS.MI}
                      </span>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>

          <ROTimeline order={order} />
        </div>

        <div className='rounded-lg border-blue-200 bg-blue-50 p-4'>
          <h3 className='mb-3 text-xs font-semibold text-gray-700 uppercase'>
            {REPAIR_ORDER_LABELS.SERVICES}
          </h3>
          <div className='flex flex-wrap gap-2'>
            {order.services.map((service, idx) => (
              <Badge
                key={idx}
                variant='default'
                className='bg-blue-600 px-2.5 py-0.5 text-xs hover:bg-blue-700'
              >
                {service}
              </Badge>
            ))}
          </div>

          {(order.estimatedDuration || order.estimatedCost) && (
            <div className='mt-4 flex gap-6 text-sm'>
              {order.estimatedDuration && (
                <div>
                  <span className='text-gray-600'>{REPAIR_ORDER_LABELS.DURATION}:</span>{' '}
                  <span className='font-semibold text-gray-900'>
                    {order.estimatedDuration} {REPAIR_ORDER_LABELS.HOURS}
                  </span>
                </div>
              )}
              {order.estimatedCost && (
                <div>
                  <span className='text-gray-600'>{REPAIR_ORDER_LABELS.COST}:</span>{' '}
                  <span className='font-semibold text-gray-900'>
                    ${order.estimatedCost.toLocaleString()}
                  </span>
                </div>
              )}
            </div>
          )}
        </div>

        <div className='rounded-lg bg-gray-50 p-6'>
          <h3 className='mb-4 text-sm font-semibold tracking-wide text-gray-700 uppercase'>
            Quick Actions
          </h3>

          <div className='grid gap-4 md:grid-cols-3'>
            {/* Status */}
            <div className='flex flex-col gap-2'>
              <Label htmlFor='status' className='text-xs font-semibold text-gray-600'>
                {REPAIR_ORDER_LABELS.STATUS}
              </Label>
              <Select
                value={currentStatus}
                onValueChange={(value) => setValue('status', value as RepairOrderStatus)}
              >
                <SelectTrigger>
                  <SelectValue>
                    <Badge
                      className={
                        STATUS_COLORS[
                          (currentStatus as RepairOrderStatus) || order.status
                        ]
                      }
                    >
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

            {/* Assigned Technician */}
            <div className='flex flex-col gap-2'>
              <Label
                htmlFor='assignedTech'
                className='text-xs font-semibold text-gray-600'
              >
                {REPAIR_ORDER_LABELS.ASSIGNED_TECHNICIAN}
              </Label>
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
            <div className='flex flex-col gap-2'>
              <Label htmlFor='priority' className='text-xs font-semibold text-gray-600'>
                {REPAIR_ORDER_LABELS.PRIORITY}
              </Label>
              <Select
                value={watch('priority')}
                onValueChange={(value) => setValue('priority', value as Priority)}
              >
                <SelectTrigger>
                  <SelectValue>
                    <Badge
                      variant='outline'
                      className={
                        PRIORITY_COLORS[(watch('priority') as Priority) || 'NORMAL']
                      }
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
          </div>

          {/* Notes - Full width */}
          <div className='mt-6 flex flex-col gap-2'>
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
        </div>

        <div className='mt-auto rounded-lg border border-amber-300 bg-amber-50 p-4'>
          <div className='flex items-start gap-4'>
            <Checkbox
              id='approvedByCustomer'
              checked={approvedByCustomer}
              onCheckedChange={(checked) =>
                setValue('approvedByCustomer', checked === true)
              }
              className='mt-1 h-5 w-5'
            />
            <div className='flex-1'>
              <Label
                htmlFor='approvedByCustomer'
                className='cursor-pointer text-base font-semibold text-gray-900'
              >
                {REPAIR_ORDER_LABELS.APPROVED_BY_CUSTOMER}
              </Label>
              <p className='mt-1 text-sm text-gray-700'>
                Customer authorization required before starting work. This confirms the
                customer has reviewed and approved the work to be performed.
              </p>
            </div>
          </div>
        </div>
      </div>

      <div className='flex shrink-0 gap-3 border-t bg-white px-6 py-3 shadow-[0_-2px_8px_rgba(0,0,0,0.08)]'>
        <Button
          type='button'
          variant='outline'
          onClick={onDelete}
          disabled={isPending || isDeleting}
          className='border-red-200 text-red-600 hover:bg-red-50 hover:text-red-700'
        >
          {isDeleting ? COMMON_LABELS.DELETING : COMMON_LABELS.DELETE}
        </Button>

        <div className='flex-1' />

        <Button
          type='submit'
          disabled={isPending || isDeleting}
          className='flex items-center gap-2 bg-orange-600 px-8 hover:bg-orange-700'
        >
          {isPending ? (
            COMMON_LABELS.SAVING
          ) : (
            <>
              {buttonConfig.icon}
              {buttonConfig.text}
            </>
          )}
        </Button>
      </div>
    </form>
  )
}
