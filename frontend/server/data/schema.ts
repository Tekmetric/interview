import db from './db.js'
import { REPAIR_ORDER_STATUSES, PRIORITIES } from '@shared/constants'

const statusCheck = REPAIR_ORDER_STATUSES.map((s) => `'${s}'`).join(', ')
const priorityCheck = PRIORITIES.map((p) => `'${p}'`).join(', ')

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
    status TEXT NOT NULL CHECK(status IN (${statusCheck})),

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
    priority TEXT NOT NULL CHECK(priority IN (${priorityCheck})) DEFAULT 'NORMAL',
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
