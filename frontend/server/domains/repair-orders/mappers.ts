import type { RepairOrder } from '@shared/types'
import { getTechnicianById } from '@server/domains/technicians/repository'

// Helper function to convert DB row to RepairOrder
export function rowToRepairOrder(row: any): RepairOrder {
  const tech = row.technician_id ? getTechnicianById(row.technician_id) : null
  return {
    id: row.id,
    status: row.status,
    customer: {
      name: row.customer_name,
      phone: row.customer_phone,
      email: row.customer_email,
    },
    vehicle: {
      year: row.vehicle_year,
      make: row.vehicle_make,
      model: row.vehicle_model,
      trim: row.vehicle_trim,
      vin: row.vehicle_vin,
      plate: row.vehicle_plate,
      mileage: row.vehicle_mileage,
      color: row.vehicle_color,
    },
    services: JSON.parse(row.services),
    assignedTech: tech,
    priority: row.priority,
    estimatedDuration: row.estimated_duration,
    estimatedCost: row.estimated_cost,
    dueTime: row.due_time,
    notes: row.notes,
    approvedByCustomer: row.approved_by_customer === 1,
    createdAt: row.created_at,
    updatedAt: row.updated_at,
  }
}

