import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Input } from '@/components/ui/input'
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
  Edit2,
  X as XIcon,
} from 'lucide-react'
import { ROTimeline } from './ro-timeline'
import { ServiceSelector } from './fields/ServiceSelector'
import { useTechnicians } from '@/components/technician/hooks/useTechnicians'
import { canTransition, ALLOWED_TRANSITIONS } from '@shared/transitions'
import type { RepairOrder, RepairOrderStatus, Priority } from '@shared/types'
import { updateRepairOrderSchema } from '@shared/validation'
import { STATUS_COLORS, PRIORITY_COLORS } from './ro-constants'
import { REPAIR_ORDER_LABELS, COMMON_LABELS, RO_STATUS } from '@shared/constants'

type RODetailsFormProps = {
  order: RepairOrder
  onSubmit: (
    data: z.infer<typeof updateRepairOrderSchema>,
    wasInEditMode: boolean,
  ) => void
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
  const [editMode, setEditMode] = useState(false)
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
      customer: order.customer,
      vehicle: order.vehicle,
      services: order.services,
      assignedTech: order.assignedTech ? { id: order.assignedTech.id } : null,
      priority: order.priority,
      estimatedDuration: order.estimatedDuration,
      estimatedCost: order.estimatedCost,
      dueTime: order.dueTime,
      notes: order.notes,
      approvedByCustomer: order.approvedByCustomer,
    },
  })

  const currentStatus = watch('status')
  const approvedByCustomer = watch('approvedByCustomer')
  const services = watch('services') || order.services
  const currentYear = new Date().getFullYear()

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

  const handleFormSubmit = (data: z.infer<typeof updateRepairOrderSchema>) => {
    onSubmit(data, editMode)
    if (editMode) {
      setEditMode(false)
    }
  }

  return (
    <form
      onSubmit={handleSubmit(handleFormSubmit)}
      className='flex h-[calc(100vh-70px)] flex-col overflow-hidden'
      aria-label='Repair order details form'
    >
      <div className='flex flex-1 flex-col gap-6 overflow-y-auto px-6 py-4'>
        <div className='flex items-center justify-between'>
          <h3 className='text-sm font-semibold text-gray-700'>
            {editMode ? 'Edit Mode' : 'Quick Actions Mode'}
          </h3>
          <Button
            type='button'
            variant={editMode ? 'outline' : 'secondary'}
            size='sm'
            onClick={() => setEditMode(!editMode)}
            className='flex items-center gap-2'
          >
            {editMode ? (
              <>
                <XIcon className='h-4 w-4' />
                Cancel Edit
              </>
            ) : (
              <>
                <Edit2 className='h-4 w-4' />
                Edit Order
              </>
            )}
          </Button>
        </div>

        <div className='flex flex-col gap-6'>
          <div className='grid grid-cols-1 gap-4 md:grid-cols-2'>
            <div
              className='flex flex-col gap-3 rounded-lg bg-gray-50 p-4'
              role='group'
              aria-labelledby='vehicle-info-heading'
            >
              <div className='flex items-center gap-2'>
                <Car className='h-4 w-4 text-gray-500' aria-hidden='true' />
                <h3
                  id='vehicle-info-heading'
                  className='text-xs font-semibold text-gray-500 uppercase'
                >
                  {REPAIR_ORDER_LABELS.VEHICLE}
                </h3>
              </div>
              {editMode ? (
                <div className='flex flex-col gap-3'>
                  <div className='grid grid-cols-2 gap-2'>
                    <div className='flex flex-col gap-1'>
                      <Label htmlFor='vehicle.year' className='text-xs'>
                        Year
                      </Label>
                      <Input
                        id='vehicle.year'
                        type='number'
                        {...register('vehicle.year', { valueAsNumber: true })}
                        min={1900}
                        max={currentYear + 1}
                      />
                      {errors.vehicle?.year && (
                        <p className='text-xs text-red-600'>
                          {errors.vehicle.year.message}
                        </p>
                      )}
                    </div>
                    <div className='flex flex-col gap-1'>
                      <Label htmlFor='vehicle.make' className='text-xs'>
                        Make
                      </Label>
                      <Input id='vehicle.make' {...register('vehicle.make')} />
                      {errors.vehicle?.make && (
                        <p className='text-xs text-red-600'>
                          {errors.vehicle.make.message}
                        </p>
                      )}
                    </div>
                  </div>
                  <div className='grid grid-cols-2 gap-2'>
                    <div className='flex flex-col gap-1'>
                      <Label htmlFor='vehicle.model' className='text-xs'>
                        Model
                      </Label>
                      <Input id='vehicle.model' {...register('vehicle.model')} />
                      {errors.vehicle?.model && (
                        <p className='text-xs text-red-600'>
                          {errors.vehicle.model.message}
                        </p>
                      )}
                    </div>
                    <div className='flex flex-col gap-1'>
                      <Label htmlFor='vehicle.trim' className='text-xs'>
                        Trim
                      </Label>
                      <Input id='vehicle.trim' {...register('vehicle.trim')} />
                    </div>
                  </div>
                  <div className='flex flex-col gap-1'>
                    <Label htmlFor='vehicle.vin' className='text-xs'>
                      VIN
                    </Label>
                    <Input
                      id='vehicle.vin'
                      {...register('vehicle.vin')}
                      maxLength={17}
                      className='font-mono text-xs'
                    />
                    {errors.vehicle?.vin && (
                      <p className='text-xs text-red-600'>{errors.vehicle.vin.message}</p>
                    )}
                  </div>
                  <div className='grid grid-cols-2 gap-2'>
                    <div className='flex flex-col gap-1'>
                      <Label htmlFor='vehicle.plate' className='text-xs'>
                        Plate
                      </Label>
                      <Input id='vehicle.plate' {...register('vehicle.plate')} />
                    </div>
                    <div className='flex flex-col gap-1'>
                      <Label htmlFor='vehicle.mileage' className='text-xs'>
                        Mileage
                      </Label>
                      <Input
                        id='vehicle.mileage'
                        type='number'
                        {...register('vehicle.mileage', { valueAsNumber: true })}
                        min={0}
                      />
                    </div>
                  </div>
                  <div className='flex flex-col gap-1'>
                    <Label htmlFor='vehicle.color' className='text-xs'>
                      Color
                    </Label>
                    <Input id='vehicle.color' {...register('vehicle.color')} />
                  </div>
                </div>
              ) : (
                <div className='flex flex-col gap-1'>
                  <p className='text-base font-semibold text-gray-900'>
                    {order.vehicle.year} {order.vehicle.make} {order.vehicle.model}
                  </p>
                  {order.vehicle.vin && (
                    <div className='flex items-center gap-1.5 text-sm text-gray-600'>
                      <Hash className='h-3.5 w-3.5' />
                      <span className='font-mono text-xs'>{order.vehicle.vin}</span>
                    </div>
                  )}
                  <div className='flex items-center gap-4 text-sm text-gray-600'>
                    {order.vehicle.plate && (
                      <div className='flex items-center gap-1.5'>
                        <CreditCard className='h-3.5 w-3.5' />
                        <span>{order.vehicle.plate}</span>
                      </div>
                    )}
                    {order.vehicle.mileage && (
                      <div className='flex items-center gap-1.5'>
                        <Gauge className='h-3.5 w-3.5' />
                        <span>{order.vehicle.mileage.toLocaleString()} mi</span>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>
            <div
              className='flex flex-col gap-3 rounded-lg bg-gray-50 p-4'
              role='group'
              aria-labelledby='customer-info-heading'
            >
              <div className='flex items-center gap-2'>
                <User className='h-4 w-4 text-gray-500' aria-hidden='true' />
                <h3
                  id='customer-info-heading'
                  className='text-xs font-semibold text-gray-500 uppercase'
                >
                  {REPAIR_ORDER_LABELS.CUSTOMER}
                </h3>
              </div>
              {editMode ? (
                <div className='flex flex-col gap-3'>
                  <div className='flex flex-col gap-1'>
                    <Label htmlFor='customer.name' className='text-xs'>
                      Name
                    </Label>
                    <Input id='customer.name' {...register('customer.name')} />
                    {errors.customer?.name && (
                      <p className='text-xs text-red-600'>
                        {errors.customer.name.message}
                      </p>
                    )}
                  </div>
                  <div className='flex flex-col gap-1'>
                    <Label htmlFor='customer.phone' className='text-xs'>
                      Phone
                    </Label>
                    <Input
                      id='customer.phone'
                      type='tel'
                      {...register('customer.phone')}
                    />
                    {errors.customer?.phone && (
                      <p className='text-xs text-red-600'>
                        {errors.customer.phone.message}
                      </p>
                    )}
                  </div>
                  <div className='flex flex-col gap-1'>
                    <Label htmlFor='customer.email' className='text-xs'>
                      Email
                    </Label>
                    <Input
                      id='customer.email'
                      type='email'
                      {...register('customer.email')}
                    />
                    {errors.customer?.email && (
                      <p className='text-xs text-red-600'>
                        {errors.customer.email.message}
                      </p>
                    )}
                  </div>
                </div>
              ) : (
                <div className='flex flex-col gap-1'>
                  <p className='text-base font-semibold text-gray-900'>
                    {order.customer.name}
                  </p>
                  <div className='flex items-center gap-1.5 text-sm text-gray-600'>
                    <Phone className='h-3.5 w-3.5' />
                    <span>{order.customer.phone}</span>
                  </div>
                  {order.customer.email && (
                    <div className='flex items-center gap-1.5 text-sm text-gray-600'>
                      <Mail className='h-3.5 w-3.5' />
                      <span>{order.customer.email}</span>
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>

          <ROTimeline order={order} />
        </div>

        <div
          className='rounded-lg border-blue-200 bg-blue-50 p-4'
          role='region'
          aria-labelledby='services-heading'
        >
          <h3
            id='services-heading'
            className='mb-3 text-xs font-semibold text-gray-700 uppercase'
          >
            {REPAIR_ORDER_LABELS.SERVICES}
          </h3>
          {editMode ? (
            <ServiceSelector
              services={services}
              setValue={setValue as any}
              errors={errors as any}
            />
          ) : (
            <div className='flex flex-wrap gap-2' role='list'>
              {order.services.map((service, idx) => (
                <Badge
                  key={idx}
                  variant='default'
                  className='bg-blue-600 px-2.5 py-0.5 text-xs hover:bg-blue-700'
                  role='listitem'
                >
                  {service}
                </Badge>
              ))}
            </div>
          )}

          <div className='mt-4 grid grid-cols-2 gap-4'>
            {editMode ? (
              <>
                <div className='flex flex-col gap-1'>
                  <Label htmlFor='estimatedDuration' className='text-xs'>
                    Duration (hours)
                  </Label>
                  <Input
                    id='estimatedDuration'
                    type='number'
                    {...register('estimatedDuration', { valueAsNumber: true })}
                    min={0}
                  />
                  {errors.estimatedDuration && (
                    <p className='text-xs text-red-600'>
                      {errors.estimatedDuration.message}
                    </p>
                  )}
                </div>
                <div className='flex flex-col gap-1'>
                  <Label htmlFor='estimatedCost' className='text-xs'>
                    Cost ($)
                  </Label>
                  <Input
                    id='estimatedCost'
                    type='number'
                    {...register('estimatedCost', { valueAsNumber: true })}
                    min={0}
                  />
                  {errors.estimatedCost && (
                    <p className='text-xs text-red-600'>{errors.estimatedCost.message}</p>
                  )}
                </div>
              </>
            ) : (
              <>
                {order.estimatedDuration && (
                  <div className='text-sm'>
                    <span className='text-gray-600'>{REPAIR_ORDER_LABELS.DURATION}:</span>{' '}
                    <span className='font-semibold text-gray-900'>
                      {order.estimatedDuration} {REPAIR_ORDER_LABELS.HOURS}
                    </span>
                  </div>
                )}
                {order.estimatedCost && (
                  <div className='text-sm'>
                    <span className='text-gray-600'>{REPAIR_ORDER_LABELS.COST}:</span>{' '}
                    <span className='font-semibold text-gray-900'>
                      ${order.estimatedCost.toLocaleString()}
                    </span>
                  </div>
                )}
              </>
            )}
          </div>
        </div>

        <div
          className='rounded-lg bg-gray-50 p-6'
          role='region'
          aria-labelledby='quick-actions-heading'
        >
          <h3
            id='quick-actions-heading'
            className='mb-4 text-sm font-semibold tracking-wide text-gray-700 uppercase'
          >
            Quick Actions
          </h3>

          <div className='grid gap-4 md:grid-cols-3'>
            <div className='flex flex-col gap-2'>
              <Label htmlFor='status' className='text-xs font-semibold text-gray-600'>
                {REPAIR_ORDER_LABELS.STATUS}
              </Label>
              <Select
                value={currentStatus}
                onValueChange={(value) => setValue('status', value as RepairOrderStatus)}
              >
                <SelectTrigger id='status' aria-label='Select repair order status'>
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
                <SelectTrigger id='assignedTech' aria-label='Select assigned technician'>
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

            <div className='flex flex-col gap-2'>
              <Label htmlFor='priority' className='text-xs font-semibold text-gray-600'>
                {REPAIR_ORDER_LABELS.PRIORITY}
              </Label>
              <Select
                value={watch('priority')}
                onValueChange={(value) => setValue('priority', value as Priority)}
              >
                <SelectTrigger id='priority' aria-label='Select priority level'>
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

          <div className='mt-6 flex flex-col gap-2'>
            <Label htmlFor='notes' className='sr-only'>
              Notes
            </Label>
            <Textarea
              id='notes'
              placeholder={REPAIR_ORDER_LABELS.ADD_NOTES_PLACEHOLDER}
              rows={4}
              {...register('notes')}
              aria-label='Additional notes'
            />
            {errors.notes && (
              <p className='text-xs text-red-600' role='alert'>
                {errors.notes.message}
              </p>
            )}
          </div>
        </div>

        <div
          className='mt-auto rounded-lg border border-amber-300 bg-amber-50 p-4'
          role='group'
          aria-labelledby='customer-approval-heading'
        >
          <div className='flex items-start gap-4'>
            <Checkbox
              id='approvedByCustomer'
              checked={approvedByCustomer}
              onCheckedChange={(checked) =>
                setValue('approvedByCustomer', checked === true)
              }
              className='mt-1 h-5 w-5'
              aria-describedby='customer-approval-description'
            />
            <div className='flex-1'>
              <Label
                htmlFor='approvedByCustomer'
                id='customer-approval-heading'
                className='cursor-pointer text-base font-semibold text-gray-900'
              >
                {REPAIR_ORDER_LABELS.APPROVED_BY_CUSTOMER}
              </Label>
              <p
                id='customer-approval-description'
                className='mt-1 text-sm text-gray-700'
              >
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
          variant='link'
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
