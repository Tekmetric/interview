import { z } from 'zod'

export const customerSchema = z
  .object({
    id: z.string(),
    name: z.string(),
    phone: z.string(),
    email: z.email(),
  })
  .meta({ id: 'Customer' })

export const vehicleSchema = z
  .object({
    id: z.string(),
    customerId: z.string(),
    year: z.number().int(),
    make: z.string(),
    model: z.string(),
    vin: z.string(),
    mileage: z.number().int(),
  })
  .meta({ id: 'Vehicle' })

export const repairOrderStatusSchema = z.enum(['new', 'in_progress', 'waiting_parts', 'completed'])

export const repairOrderSchema = z
  .object({
    id: z.string(),
    vehicleId: z.string(),
    status: repairOrderStatusSchema,
    openedAt: z.iso.datetime(),
    laborTotal: z.number(),
    partsTotal: z.number(),
    tax: z.number(),
    grandTotal: z.number(),
  })
  .meta({ id: 'RepairOrder' })

export const dashboardKpisSchema = z
  .object({
    carCountToday: z.number().int(),
    revenueToday: z.number(),
    averageRO: z.number(),
    openROs: z.number().int(),
  })
  .meta({ id: 'DashboardKpis' })

export const healthSchema = z
  .object({
    status: z.literal('ok'),
  })
  .meta({ id: 'Health' })

export const notFoundSchema = z
  .object({
    message: z.string(),
  })
  .meta({ id: 'NotFound' })

export const customerIdParamsSchema = z.object({
  customerId: z.string(),
})

export const vehicleIdParamsSchema = z.object({
  vehicleId: z.string(),
})

export const repairOrderIdParamsSchema = z.object({
  roId: z.string(),
})
