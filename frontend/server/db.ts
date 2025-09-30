import Database from 'better-sqlite3'
import { fileURLToPath } from 'url'
import { dirname, join } from 'path'

const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)

const db = new Database(join(__dirname, 'tekboard.db'))

// Enable foreign keys
db.pragma('foreign_keys = ON')

// Create tables
db.exec(`
  CREATE TABLE IF NOT EXISTS technicians (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    initials TEXT NOT NULL,
    specialties TEXT DEFAULT '[]',
    active INTEGER DEFAULT 1
  );

  CREATE TABLE IF NOT EXISTS repair_orders (
    id TEXT PRIMARY KEY,
    status TEXT NOT NULL CHECK(status IN ('NEW', 'AWAITING_APPROVAL', 'IN_PROGRESS', 'WAITING_PARTS', 'COMPLETED')),
    
    customer_name TEXT NOT NULL,
    customer_phone TEXT NOT NULL,
    customer_email TEXT,
    
    vehicle_year INTEGER NOT NULL,
    vehicle_make TEXT NOT NULL,
    vehicle_model TEXT NOT NULL,
    vehicle_trim TEXT,
    vehicle_vin TEXT,
    vehicle_plate TEXT,
    vehicle_mileage INTEGER,
    vehicle_color TEXT,
    
    services TEXT NOT NULL DEFAULT '[]',
    technician_id TEXT,
    priority TEXT NOT NULL CHECK(priority IN ('HIGH', 'NORMAL')) DEFAULT 'NORMAL',
    estimated_duration INTEGER,
    estimated_cost INTEGER,
    due_time TEXT,
    notes TEXT DEFAULT '',
    approved_by_customer INTEGER DEFAULT 0,
    
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now')),
    
    FOREIGN KEY (technician_id) REFERENCES technicians(id)
  );

  CREATE INDEX IF NOT EXISTS idx_repair_orders_status ON repair_orders(status);
  CREATE INDEX IF NOT EXISTS idx_repair_orders_tech ON repair_orders(technician_id);
  CREATE INDEX IF NOT EXISTS idx_repair_orders_due ON repair_orders(due_time);
`)

export type RepairOrderStatus = 'NEW' | 'AWAITING_APPROVAL' | 'IN_PROGRESS' | 'WAITING_PARTS' | 'COMPLETED'
export type Priority = 'HIGH' | 'NORMAL'

export interface Technician {
  id: string
  name: string
  initials: string
  specialties: string[]
  active: boolean
}

export interface RepairOrder {
  id: string
  status: RepairOrderStatus
  customer: {
    name: string
    phone: string
    email?: string
  }
  vehicle: {
    year: number
    make: string
    model: string
    trim?: string
    vin?: string
    plate?: string
    mileage?: number
    color?: string
  }
  services: string[]
  assignedTech: Technician | null
  priority: Priority
  estimatedDuration?: number
  estimatedCost?: number
  dueTime?: string
  notes: string
  approvedByCustomer: boolean
  createdAt: string
  updatedAt: string
}

// Database functions
export function getAllTechnicians(): Technician[] {
  const stmt = db.prepare('SELECT * FROM technicians WHERE active = 1')
  const rows = stmt.all() as any[]
  return rows.map(row => ({
    id: row.id,
    name: row.name,
    initials: row.initials,
    specialties: JSON.parse(row.specialties),
    active: row.active === 1
  }))
}

export function getTechnicianById(id: string): Technician | null {
  const stmt = db.prepare('SELECT * FROM technicians WHERE id = ?')
  const row = stmt.get(id) as any
  if (!row) return null
  return {
    id: row.id,
    name: row.name,
    initials: row.initials,
    specialties: JSON.parse(row.specialties),
    active: row.active === 1
  }
}

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
    data.notes || ''
  )
  
  return getRepairOrderById(id)!
}

export function updateRepairOrder(id: string, data: Partial<RepairOrder>): RepairOrder | null {
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
  
  updates.push('updated_at = datetime("now")')
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

export function insertTechnician(tech: Technician): void {
  const stmt = db.prepare(`
    INSERT INTO technicians (id, name, initials, specialties, active)
    VALUES (?, ?, ?, ?, ?)
  `)
  stmt.run(tech.id, tech.name, tech.initials, JSON.stringify(tech.specialties), tech.active ? 1 : 0)
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
    order.id, order.status, order.customer_name, order.customer_phone, order.customer_email,
    order.vehicle_year, order.vehicle_make, order.vehicle_model, order.vehicle_trim,
    order.vehicle_vin, order.vehicle_plate, order.vehicle_mileage, order.vehicle_color,
    order.services, order.technician_id, order.priority, order.estimated_duration,
    order.estimated_cost, order.due_time, order.notes, order.approved_by_customer,
    order.created_at
  )
}

// Helper function to convert DB row to RepairOrder
function rowToRepairOrder(row: any): RepairOrder {
  const tech = row.technician_id ? getTechnicianById(row.technician_id) : null
  return {
    id: row.id,
    status: row.status,
    customer: {
      name: row.customer_name,
      phone: row.customer_phone,
      email: row.customer_email
    },
    vehicle: {
      year: row.vehicle_year,
      make: row.vehicle_make,
      model: row.vehicle_model,
      trim: row.vehicle_trim,
      vin: row.vehicle_vin,
      plate: row.vehicle_plate,
      mileage: row.vehicle_mileage,
      color: row.vehicle_color
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
    updatedAt: row.updated_at
  }
}

export default db
