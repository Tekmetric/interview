import db from '@server/data/db'
import type { RepairOrder } from '@shared/types'
import { rowToRepairOrder } from './mappers'

export function getAllRepairOrders(): RepairOrder[] {
  const stmt = db.prepare('SELECT * FROM repair_orders ORDER BY created_at DESC')
  const rows = stmt.all() as any[]
  return rows.map(rowToRepairOrder)
}

export function getRepairOrderById(id: string): RepairOrder | null {
  const stmt = db.prepare('SELECT * FROM repair_orders WHERE id = ?')
  const row = stmt.get(id) as any
  if (!row) return null
  return rowToRepairOrder(row)
}

export function createRepairOrder(data: Partial<RepairOrder>): RepairOrder {
  const id = `RO-${Date.now()}`
  const stmt = db.prepare(`
    INSERT INTO repair_orders (
      id, status, customer_name, customer_phone, customer_email,
      vehicle_year, vehicle_make, vehicle_model, vehicle_trim,
      vehicle_vin, vehicle_plate, vehicle_mileage, vehicle_color,
      services, technician_id, priority, estimated_duration,
      estimated_cost, due_time, notes
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  `)

  stmt.run(
    id,
    data.status || 'NEW',
    data.customer?.name || '',
    data.customer?.phone || '',
    data.customer?.email || null,
    data.vehicle?.year || 2020,
    data.vehicle?.make || '',
    data.vehicle?.model || '',
    data.vehicle?.trim || null,
    data.vehicle?.vin || null,
    data.vehicle?.plate || null,
    data.vehicle?.mileage || null,
    data.vehicle?.color || null,
    JSON.stringify(data.services || []),
    data.assignedTech?.id || null,
    data.priority || 'NORMAL',
    data.estimatedDuration || null,
    data.estimatedCost || null,
    data.dueTime || null,
    data.notes || '',
  )

  return getRepairOrderById(id)!
}

export function updateRepairOrder(
  id: string,
  data: Partial<RepairOrder>,
): RepairOrder | null {
  const updates: string[] = []
  const values: any[] = []

  if (data.status !== undefined) {
    updates.push('status = ?')
    values.push(data.status)
  }
  if (data.assignedTech !== undefined) {
    updates.push('technician_id = ?')
    values.push(data.assignedTech?.id || null)
  }
  if (data.notes !== undefined) {
    updates.push('notes = ?')
    values.push(data.notes)
  }
  if (data.approvedByCustomer !== undefined) {
    updates.push('approved_by_customer = ?')
    values.push(data.approvedByCustomer ? 1 : 0)
  }

  if (updates.length === 0) return getRepairOrderById(id)

  updates.push(`updated_at = datetime('now')`)
  values.push(id)

  const stmt = db.prepare(`
    UPDATE repair_orders
    SET ${updates.join(', ')}
    WHERE id = ?
  `)

  stmt.run(...values)
  return getRepairOrderById(id)
}

export function deleteRepairOrder(id: string): boolean {
  const stmt = db.prepare('DELETE FROM repair_orders WHERE id = ?')
  const result = stmt.run(id)
  return result.changes > 0
}

export function insertRepairOrderDirect(order: any): void {
  const stmt = db.prepare(`
    INSERT INTO repair_orders (
      id, status, customer_name, customer_phone, customer_email,
      vehicle_year, vehicle_make, vehicle_model, vehicle_trim,
      vehicle_vin, vehicle_plate, vehicle_mileage, vehicle_color,
      services, technician_id, priority, estimated_duration,
      estimated_cost, due_time, notes, approved_by_customer,
      created_at
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  `)
  stmt.run(
    order.id,
    order.status,
    order.customer_name,
    order.customer_phone,
    order.customer_email,
    order.vehicle_year,
    order.vehicle_make,
    order.vehicle_model,
    order.vehicle_trim,
    order.vehicle_vin,
    order.vehicle_plate,
    order.vehicle_mileage,
    order.vehicle_color,
    order.services,
    order.technician_id,
    order.priority,
    order.estimated_duration,
    order.estimated_cost,
    order.due_time,
    order.notes,
    order.approved_by_customer,
    order.created_at,
  )
}
