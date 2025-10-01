import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import Database from 'better-sqlite3'
import { createTestDb, cleanupDb, seedTestTechnicians } from '@server/tests/test-utils'
import { rowToRepairOrder } from '../mappers'

// Mock the db imports
const mockDb = { current: null as Database.Database | null }

vi.mock('@server/data/db', () => ({
  default: new Proxy(
    {},
    {
      get(_target, prop) {
        if (mockDb.current) {
          return mockDb.current[prop as keyof Database.Database]
        }
        throw new Error('Database not initialized in test')
      },
    },
  ),
}))

describe('Repair Orders Mappers', () => {
  beforeEach(() => {
    mockDb.current = createTestDb()
    seedTestTechnicians(mockDb.current)
  })

  afterEach(() => {
    if (mockDb.current) {
      cleanupDb(mockDb.current)
      mockDb.current = null
    }
  })

  describe('rowToRepairOrder', () => {
    it('should transform a complete DB row to RepairOrder', () => {
      const row = {
        id: 'RO-1',
        status: 'IN_PROGRESS',
        customer_name: 'John Customer',
        customer_phone: '555-1234',
        customer_email: 'john@example.com',
        vehicle_year: 2020,
        vehicle_make: 'Toyota',
        vehicle_model: 'Camry',
        vehicle_trim: 'LE',
        vehicle_vin: '1HGCM82633A123456',
        vehicle_plate: 'ABC123',
        vehicle_mileage: 50000,
        vehicle_color: 'Silver',
        services: JSON.stringify(['Oil Change', 'Tire Rotation']),
        technician_id: 'tech-1',
        priority: 'HIGH',
        estimated_duration: 120,
        estimated_cost: 250,
        due_time: '2024-01-15 10:00:00',
        notes: 'Customer waiting',
        approved_by_customer: 1,
        created_at: '2024-01-01 09:00:00',
        updated_at: '2024-01-01 10:00:00',
      }

      const result = rowToRepairOrder(row)

      expect(result).toEqual({
        id: 'RO-1',
        status: 'IN_PROGRESS',
        customer: {
          name: 'John Customer',
          phone: '555-1234',
          email: 'john@example.com',
        },
        vehicle: {
          year: 2020,
          make: 'Toyota',
          model: 'Camry',
          trim: 'LE',
          vin: '1HGCM82633A123456',
          plate: 'ABC123',
          mileage: 50000,
          color: 'Silver',
        },
        services: ['Oil Change', 'Tire Rotation'],
        assignedTech: {
          id: 'tech-1',
          name: 'John Doe',
          initials: 'JD',
          specialties: ['engine', 'transmission'],
          active: true,
        },
        priority: 'HIGH',
        estimatedDuration: 120,
        estimatedCost: 250,
        dueTime: '2024-01-15 10:00:00',
        notes: 'Customer waiting',
        approvedByCustomer: true,
        createdAt: '2024-01-01 09:00:00',
        updatedAt: '2024-01-01 10:00:00',
      })
    })

    it('should handle null technician_id', () => {
      const row = {
        id: 'RO-2',
        status: 'NEW',
        customer_name: 'Jane Customer',
        customer_phone: '555-5678',
        customer_email: null,
        vehicle_year: 2019,
        vehicle_make: 'Honda',
        vehicle_model: 'Civic',
        vehicle_trim: null,
        vehicle_vin: null,
        vehicle_plate: null,
        vehicle_mileage: null,
        vehicle_color: null,
        services: JSON.stringify(['Brake Check']),
        technician_id: null,
        priority: 'NORMAL',
        estimated_duration: null,
        estimated_cost: null,
        due_time: null,
        notes: '',
        approved_by_customer: 0,
        created_at: '2024-01-02 10:00:00',
        updated_at: '2024-01-02 10:00:00',
      }

      const result = rowToRepairOrder(row)

      expect(result.assignedTech).toBeNull()
    })

    it('should correctly parse services JSON array', () => {
      const row = {
        id: 'RO-3',
        status: 'NEW',
        customer_name: 'Test',
        customer_phone: '555-0000',
        customer_email: null,
        vehicle_year: 2020,
        vehicle_make: 'Test',
        vehicle_model: 'Test',
        vehicle_trim: null,
        vehicle_vin: null,
        vehicle_plate: null,
        vehicle_mileage: null,
        vehicle_color: null,
        services: JSON.stringify(['Service 1', 'Service 2', 'Service 3']),
        technician_id: null,
        priority: 'NORMAL',
        estimated_duration: null,
        estimated_cost: null,
        due_time: null,
        notes: '',
        approved_by_customer: 0,
        created_at: '2024-01-01 10:00:00',
        updated_at: '2024-01-01 10:00:00',
      }

      const result = rowToRepairOrder(row)

      expect(Array.isArray(result.services)).toBe(true)
      expect(result.services).toEqual(['Service 1', 'Service 2', 'Service 3'])
    })

    it('should convert approved_by_customer integer to boolean (true)', () => {
      const row = {
        id: 'RO-4',
        status: 'COMPLETED',
        customer_name: 'Test',
        customer_phone: '555-0000',
        customer_email: null,
        vehicle_year: 2020,
        vehicle_make: 'Test',
        vehicle_model: 'Test',
        vehicle_trim: null,
        vehicle_vin: null,
        vehicle_plate: null,
        vehicle_mileage: null,
        vehicle_color: null,
        services: JSON.stringify(['Service']),
        technician_id: 'tech-1',
        priority: 'NORMAL',
        estimated_duration: null,
        estimated_cost: null,
        due_time: null,
        notes: '',
        approved_by_customer: 1,
        created_at: '2024-01-01 10:00:00',
        updated_at: '2024-01-01 10:00:00',
      }

      const result = rowToRepairOrder(row)

      expect(result.approvedByCustomer).toBe(true)
      expect(typeof result.approvedByCustomer).toBe('boolean')
    })

    it('should convert approved_by_customer integer to boolean (false)', () => {
      const row = {
        id: 'RO-5',
        status: 'NEW',
        customer_name: 'Test',
        customer_phone: '555-0000',
        customer_email: null,
        vehicle_year: 2020,
        vehicle_make: 'Test',
        vehicle_model: 'Test',
        vehicle_trim: null,
        vehicle_vin: null,
        vehicle_plate: null,
        vehicle_mileage: null,
        vehicle_color: null,
        services: JSON.stringify(['Service']),
        technician_id: null,
        priority: 'NORMAL',
        estimated_duration: null,
        estimated_cost: null,
        due_time: null,
        notes: '',
        approved_by_customer: 0,
        created_at: '2024-01-01 10:00:00',
        updated_at: '2024-01-01 10:00:00',
      }

      const result = rowToRepairOrder(row)

      expect(result.approvedByCustomer).toBe(false)
      expect(typeof result.approvedByCustomer).toBe('boolean')
    })

    it('should preserve all customer fields including nullable email', () => {
      const rowWithEmail = {
        id: 'RO-6',
        status: 'NEW',
        customer_name: 'With Email',
        customer_phone: '555-1111',
        customer_email: 'test@example.com',
        vehicle_year: 2020,
        vehicle_make: 'Test',
        vehicle_model: 'Test',
        vehicle_trim: null,
        vehicle_vin: null,
        vehicle_plate: null,
        vehicle_mileage: null,
        vehicle_color: null,
        services: JSON.stringify(['Service']),
        technician_id: null,
        priority: 'NORMAL',
        estimated_duration: null,
        estimated_cost: null,
        due_time: null,
        notes: '',
        approved_by_customer: 0,
        created_at: '2024-01-01 10:00:00',
        updated_at: '2024-01-01 10:00:00',
      }

      const result = rowToRepairOrder(rowWithEmail)

      expect(result.customer.email).toBe('test@example.com')
    })

    it('should fetch and include technician details when technician_id is present', () => {
      const row = {
        id: 'RO-7',
        status: 'IN_PROGRESS',
        customer_name: 'Test',
        customer_phone: '555-0000',
        customer_email: null,
        vehicle_year: 2020,
        vehicle_make: 'Test',
        vehicle_model: 'Test',
        vehicle_trim: null,
        vehicle_vin: null,
        vehicle_plate: null,
        vehicle_mileage: null,
        vehicle_color: null,
        services: JSON.stringify(['Service']),
        technician_id: 'tech-2',
        priority: 'NORMAL',
        estimated_duration: null,
        estimated_cost: null,
        due_time: null,
        notes: '',
        approved_by_customer: 1,
        created_at: '2024-01-01 10:00:00',
        updated_at: '2024-01-01 10:00:00',
      }

      const result = rowToRepairOrder(row)

      expect(result.assignedTech).toBeDefined()
      expect(result.assignedTech?.id).toBe('tech-2')
      expect(result.assignedTech?.name).toBe('Jane Smith')
      expect(result.assignedTech?.initials).toBe('JS')
      expect(result.assignedTech?.specialties).toEqual(['brakes', 'suspension'])
    })
  })
})

