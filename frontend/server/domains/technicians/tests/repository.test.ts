import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import Database from 'better-sqlite3'
import { createTestDb, cleanupDb, seedTestTechnicians } from '@server/tests/test-utils'
import type { Technician } from '@shared/types'

// Mock the db import
const mockDb = { current: null as Database.Database | null }

vi.mock('@server/data/db', () => ({
  default: new Proxy({}, {
    get(_target, prop) {
      if (mockDb.current) {
        return mockDb.current[prop as keyof Database.Database]
      }
      throw new Error('Database not initialized in test')
    },
  }),
}))

// Import after mock is set up
const { getAllTechnicians, getTechnicianById, insertTechnician } = await import('../repository')

describe('Technicians Repository', () => {
  beforeEach(() => {
    mockDb.current = createTestDb()
  })

  afterEach(() => {
    if (mockDb.current) {
      cleanupDb(mockDb.current)
      mockDb.current = null
    }
  })

  describe('getAllTechnicians', () => {
    it('should return all active technicians', () => {
      seedTestTechnicians(mockDb.current!)

      const technicians = getAllTechnicians()

      expect(technicians).toHaveLength(2)
      expect(technicians.every((t) => t.active)).toBe(true)
    })

    it('should correctly parse specialties JSON', () => {
      seedTestTechnicians(mockDb.current!)

      const technicians = getAllTechnicians()

      const johnDoe = technicians.find((t) => t.name === 'John Doe')
      expect(johnDoe?.specialties).toEqual(['engine', 'transmission'])
      expect(Array.isArray(johnDoe?.specialties)).toBe(true)
    })

    it('should return empty array when no technicians exist', () => {
      const technicians = getAllTechnicians()
      expect(technicians).toEqual([])
    })

    it('should not include inactive technicians', () => {
      seedTestTechnicians(mockDb.current!)

      const technicians = getAllTechnicians()

      expect(technicians.find((t) => t.name === 'Bob Inactive')).toBeUndefined()
    })
  })

  describe('getTechnicianById', () => {
    it('should return technician when found', () => {
      seedTestTechnicians(mockDb.current!)

      const tech = getTechnicianById('tech-1')

      expect(tech).toBeDefined()
      expect(tech?.id).toBe('tech-1')
      expect(tech?.name).toBe('John Doe')
      expect(tech?.initials).toBe('JD')
      expect(tech?.specialties).toEqual(['engine', 'transmission'])
      expect(tech?.active).toBe(true)
    })

    it('should return null when technician not found', () => {
      const tech = getTechnicianById('non-existent')
      expect(tech).toBeNull()
    })

    it('should return inactive technicians', () => {
      seedTestTechnicians(mockDb.current!)

      const tech = getTechnicianById('tech-3')

      expect(tech).toBeDefined()
      expect(tech?.active).toBe(false)
    })

    it('should correctly parse specialties as array', () => {
      seedTestTechnicians(mockDb.current!)

      const tech = getTechnicianById('tech-2')

      expect(Array.isArray(tech?.specialties)).toBe(true)
      expect(tech?.specialties).toEqual(['brakes', 'suspension'])
    })
  })

  describe('insertTechnician', () => {
    it('should insert a new technician', () => {
      const newTech: Technician = {
        id: 'tech-new',
        name: 'New Tech',
        initials: 'NT',
        specialties: ['diagnostics'],
        active: true,
      }

      insertTechnician(newTech)

      const retrieved = getTechnicianById('tech-new')
      expect(retrieved).toEqual(newTech)
    })

    it('should correctly serialize specialties array to JSON', () => {
      const newTech: Technician = {
        id: 'tech-array',
        name: 'Array Tech',
        initials: 'AT',
        specialties: ['paint', 'bodywork', 'detailing'],
        active: true,
      }

      insertTechnician(newTech)

      const retrieved = getTechnicianById('tech-array')
      expect(retrieved?.specialties).toEqual(['paint', 'bodywork', 'detailing'])
      expect(Array.isArray(retrieved?.specialties)).toBe(true)
    })

    it('should handle empty specialties array', () => {
      const newTech: Technician = {
        id: 'tech-empty',
        name: 'Empty Specs',
        initials: 'ES',
        specialties: [],
        active: true,
      }

      insertTechnician(newTech)

      const retrieved = getTechnicianById('tech-empty')
      expect(retrieved?.specialties).toEqual([])
    })

    it('should correctly store active status as integer', () => {
      const activeTech: Technician = {
        id: 'tech-active',
        name: 'Active Tech',
        initials: 'AT',
        specialties: [],
        active: true,
      }

      const inactiveTech: Technician = {
        id: 'tech-inactive',
        name: 'Inactive Tech',
        initials: 'IT',
        specialties: [],
        active: false,
      }

      insertTechnician(activeTech)
      insertTechnician(inactiveTech)

      // Check active tech
      const active = getTechnicianById('tech-active')
      expect(active?.active).toBe(true)

      // Check inactive tech
      const inactive = getTechnicianById('tech-inactive')
      expect(inactive?.active).toBe(false)
    })
  })
})
