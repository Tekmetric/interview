import { z } from 'zod'
import { REPAIR_ORDER_STATUSES, PRIORITIES } from './constants'

export const customerSchema = z.object({
  name: z.string().min(1, 'Customer name is required'),
  phone: z.string().min(1, 'Customer phone is required'),
  email: z.string().email('Invalid email address').optional(),
})

export const vehicleSchema = z.object({
  year: z
    .number()
    .int()
    .min(1900)
    .max(new Date().getFullYear() + 1),
  make: z.string().min(1, 'Vehicle make is required'),
  model: z.string().min(1, 'Vehicle model is required'),
  trim: z.string().optional(),
  vin: z.string().length(17, 'VIN must be 17 characters').optional(),
  plate: z.string().optional(),
  mileage: z.number().int().positive().optional(),
  color: z.string().optional(),
})

export const repairOrderStatusSchema = z.enum(
  REPAIR_ORDER_STATUSES as [string, ...string[]],
)

export const prioritySchema = z.enum(PRIORITIES as [string, ...string[]])

export const createRepairOrderSchema = z.object({
  customer: customerSchema,
  vehicle: vehicleSchema,
  services: z.array(z.string()).min(1, 'At least one service is required'),
  priority: prioritySchema.default('NORMAL'),
  estimatedDuration: z.number().int().positive().optional(),
  estimatedCost: z.number().int().positive().optional(),
  dueTime: z.string().datetime().optional(),
  notes: z.string().default(''),
})

export const updateRepairOrderSchema = z.object({
  status: repairOrderStatusSchema.optional(),
  customer: customerSchema.optional(),
  vehicle: vehicleSchema.optional(),
  services: z.array(z.string()).min(1, 'At least one service is required').optional(),
  assignedTech: z
    .object({
      id: z.string(),
    })
    .nullable()
    .optional(),
  priority: prioritySchema.optional(),
  estimatedDuration: z.number().int().positive().optional(),
  estimatedCost: z.number().int().positive().optional(),
  dueTime: z.string().datetime().optional(),
  notes: z.string().optional(),
  approvedByCustomer: z.boolean().optional(),
})

export const repairOrderFiltersSchema = z.object({
  status: repairOrderStatusSchema.optional(),
  tech: z.string().optional(),
  priority: prioritySchema.optional(),
  search: z.string().optional(),
})

export type CreateRepairOrderInput = z.infer<typeof createRepairOrderSchema>
export type UpdateRepairOrderInput = z.infer<typeof updateRepairOrderSchema>
export type RepairOrderFilters = z.infer<typeof repairOrderFiltersSchema>
