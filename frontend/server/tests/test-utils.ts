import Database from 'better-sqlite3'
import type { Technician } from '@shared/types'

// Create an in-memory test database with schema
export function createTestDb(): Database.Database {
  const db = new Database(':memory:')
  db.pragma('foreign_keys = ON')

  // Create schema
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

  return db
}

// Cleanup database connection
export function cleanupDb(db: Database.Database): void {
  if (db.open) {
    db.close()
  }
}

// Seed test technicians
export function seedTestTechnicians(db: Database.Database): Technician[] {
  const technicians: Technician[] = [
    {
      id: 'tech-1',
      name: 'John Doe',
      initials: 'JD',
      specialties: ['engine', 'transmission'],
      active: true,
    },
    {
      id: 'tech-2',
      name: 'Jane Smith',
      initials: 'JS',
      specialties: ['brakes', 'suspension'],
      active: true,
    },
    {
      id: 'tech-3',
      name: 'Bob Inactive',
      initials: 'BI',
      specialties: ['electrical'],
      active: false,
    },
  ]

  const stmt = db.prepare(`
    INSERT INTO technicians (id, name, initials, specialties, active)
    VALUES (?, ?, ?, ?, ?)
  `)

  for (const tech of technicians) {
    stmt.run(
      tech.id,
      tech.name,
      tech.initials,
      JSON.stringify(tech.specialties),
      tech.active ? 1 : 0,
    )
  }

  return technicians
}

// Seed test repair orders
export function seedTestRepairOrders(db: Database.Database) {
  const orders = [
    {
      id: 'RO-1',
      status: 'NEW',
      customer_name: 'Alice Customer',
      customer_phone: '555-1234',
      customer_email: 'alice@example.com',
      vehicle_year: 2020,
      vehicle_make: 'Toyota',
      vehicle_model: 'Camry',
      vehicle_trim: 'LE',
      vehicle_vin: '1HGCM82633A123456',
      vehicle_plate: 'ABC123',
      vehicle_mileage: 50000,
      vehicle_color: 'Silver',
      services: JSON.stringify(['Oil Change', 'Tire Rotation']),
      technician_id: null,
      priority: 'NORMAL',
      estimated_duration: 60,
      estimated_cost: 150,
      due_time: null,
      notes: 'Standard service',
      approved_by_customer: 0,
      created_at: '2024-01-01 10:00:00',
    },
    {
      id: 'RO-2',
      status: 'IN_PROGRESS',
      customer_name: 'Bob Customer',
      customer_phone: '555-5678',
      customer_email: null,
      vehicle_year: 2019,
      vehicle_make: 'Honda',
      vehicle_model: 'Civic',
      vehicle_trim: null,
      vehicle_vin: null,
      vehicle_plate: 'XYZ789',
      vehicle_mileage: 75000,
      vehicle_color: null,
      services: JSON.stringify(['Brake Replacement']),
      technician_id: 'tech-1',
      priority: 'HIGH',
      estimated_duration: 120,
      estimated_cost: 450,
      due_time: '2024-01-02 15:00:00',
      notes: 'Urgent - customer waiting',
      approved_by_customer: 1,
      created_at: '2024-01-01 09:00:00',
    },
  ]

  const stmt = db.prepare(`
    INSERT INTO repair_orders (
      id, status, customer_name, customer_phone, customer_email,
      vehicle_year, vehicle_make, vehicle_model, vehicle_trim,
      vehicle_vin, vehicle_plate, vehicle_mileage, vehicle_color,
      services, technician_id, priority, estimated_duration,
      estimated_cost, due_time, notes, approved_by_customer, created_at
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  `)

  for (const order of orders) {
    stmt.run(...Object.values(order))
  }

  return orders
}
