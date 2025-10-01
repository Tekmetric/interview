import db from '../../data/db.js'
import type { Technician } from '../../../shared/types.js'

export function getAllTechnicians(): Technician[] {
  const stmt = db.prepare('SELECT * FROM technicians WHERE active = 1')
  const rows = stmt.all() as any[]
  return rows.map((row) => ({
    id: row.id,
    name: row.name,
    initials: row.initials,
    specialties: JSON.parse(row.specialties),
    active: row.active === 1,
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
    active: row.active === 1,
  }
}

export function insertTechnician(tech: Technician): void {
  const stmt = db.prepare(`
    INSERT INTO technicians (id, name, initials, specialties, active)
    VALUES (?, ?, ?, ?, ?)
  `)
  stmt.run(
    tech.id,
    tech.name,
    tech.initials,
    JSON.stringify(tech.specialties),
    tech.active ? 1 : 0,
  )
}
