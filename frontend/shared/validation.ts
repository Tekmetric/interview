import { z } from 'zod'

// Customer validation schema
export const customerSchema = z.object({
  name: z.string().min(1, 'Customer name is required'),
  phone: z.string().min(1, 'Customer phone is required'),
  email: z.string().email('Invalid email address').optional(),
})

// Vehicle validation schema
export const vehicleSchema = z.object({
  year: z.number().int().min(1900).max(new Date().getFullYear() + 1),
  make: z.string().min(1, 'Vehicle make is required'),
  model: z.string().min(1, 'Vehicle model is required'),
  trim: z.string().optional(),
  vin: z.string().length(17, 'VIN must be 17 characters').optional(),
  plate: z.string().optional(),
  mileage: z.number().int().positive().optional(),
  color: z.string().optional(),
})

// Repair order status enum
export const repairOrderStatusSchema = z.enum([
  'NEW',
  'AWAITING_APPROVAL',
  'IN_PROGRESS',
  'WAITING_PARTS',
  'COMPLETED',
])

// Priority enum
export const prioritySchema = z.enum(['HIGH', 'NORMAL'])

// Create repair order schema
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

// Update repair order schema (all fields optional)
export const updateRepairOrderSchema = z.object({
  status: repairOrderStatusSchema.optional(),
  assignedTech: z
    .object({
      id: z.string(),
    })
    .nullable()
    .optional(),
  notes: z.string().optional(),
  approvedByCustomer: z.boolean().optional(),
})

// Query filters schema
export const repairOrderFiltersSchema = z.object({
  status: repairOrderStatusSchema.optional(),
  tech: z.string().optional(),
  priority: prioritySchema.optional(),
  search: z.string().optional(),
})

// Export types
export type CreateRepairOrderInput = z.infer<typeof createRepairOrderSchema>
export type UpdateRepairOrderInput = z.infer<typeof updateRepairOrderSchema>
export type RepairOrderFilters = z.infer<typeof repairOrderFiltersSchema>
