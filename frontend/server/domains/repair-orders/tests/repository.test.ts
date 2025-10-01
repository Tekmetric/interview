import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import Database from 'better-sqlite3'
import {
  createTestDb,
  cleanupDb,
  seedTestTechnicians,
  seedTestRepairOrders,
} from '@server/tests/test-utils'
import type { RepairOrder } from '@shared/types'

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

const {
  getAllRepairOrders,
  getRepairOrderById,
  createRepairOrder,
  updateRepairOrder,
  deleteRepairOrder,
} = await import('../repository')

describe('Repair Orders Repository', () => {
  beforeEach(() => {
    mockDb.current = createTestDb()
    seedTestTechnicians(mockDb.current)
    seedTestRepairOrders(mockDb.current)
  })

  afterEach(() => {
    if (mockDb.current) {
      cleanupDb(mockDb.current)
      mockDb.current = null
    }
  })

  describe('getAllRepairOrders', () => {
    it('should return all repair orders ordered by created_at DESC', () => {
      const orders = getAllRepairOrders()

      expect(orders).toHaveLength(2)
      // RO-1 was created after RO-2, so should come first
      expect(orders[0].id).toBe('RO-1')
      expect(orders[1].id).toBe('RO-2')
    })

    it('should return empty array when no orders exist', () => {
      // Clear the database
      mockDb.current!.exec('DELETE FROM repair_orders')

      const orders = getAllRepairOrders()
      expect(orders).toEqual([])
    })

    it('should transform all rows correctly', () => {
      const orders = getAllRepairOrders()

      orders.forEach((order) => {
        expect(order).toHaveProperty('id')
        expect(order).toHaveProperty('status')
        expect(order).toHaveProperty('customer')
        expect(order).toHaveProperty('vehicle')
        expect(order).toHaveProperty('services')
        expect(Array.isArray(order.services)).toBe(true)
      })
    })
  })

  describe('getRepairOrderById', () => {
    it('should return repair order when found', () => {
      const order = getRepairOrderById('RO-1')

      expect(order).toBeDefined()
      expect(order?.id).toBe('RO-1')
      expect(order?.customer.name).toBe('Alice Customer')
      expect(order?.vehicle.make).toBe('Toyota')
      expect(order?.status).toBe('NEW')
    })

    it('should return null when order not found', () => {
      const order = getRepairOrderById('NON-EXISTENT')
      expect(order).toBeNull()
    })

    it('should include assigned technician details', () => {
      const order = getRepairOrderById('RO-2')

      expect(order?.assignedTech).toBeDefined()
      expect(order?.assignedTech?.id).toBe('tech-1')
      expect(order?.assignedTech?.name).toBe('John Doe')
    })
  })

  describe('createRepairOrder', () => {
    it('should create a new repair order with generated ID', () => {
      const newOrder: Partial<RepairOrder> = {
        status: 'NEW',
        customer: {
          name: 'New Customer',
          phone: '555-9999',
          email: 'new@example.com',
        },
        vehicle: {
          year: 2021,
          make: 'Ford',
          model: 'F-150',
          trim: 'XLT',
        },
        services: ['Engine Repair'],
        priority: 'HIGH',
        notes: 'New order test',
      }

      const created = createRepairOrder(newOrder)

      expect(created).toBeDefined()
      expect(created.id).toMatch(/^RO-\d+$/)
      expect(created.customer.name).toBe('New Customer')
      expect(created.vehicle.make).toBe('Ford')
      expect(created.status).toBe('NEW')
      expect(created.services).toEqual(['Engine Repair'])
    })

    it('should use default values for optional fields', () => {
      const minimalOrder: Partial<RepairOrder> = {
        customer: {
          name: 'Minimal Customer',
          phone: '555-0000',
        },
        vehicle: {
          year: 2020,
          make: 'Test',
          model: 'Test',
        },
        services: ['Test Service'],
      }

      const created = createRepairOrder(minimalOrder)

      expect(created.status).toBe('NEW')
      expect(created.priority).toBe('NORMAL')
      expect(created.notes).toBe('')
      expect(created.assignedTech).toBeNull()
    })

    it('should persist created order to database', () => {
      const newOrder: Partial<RepairOrder> = {
        customer: {
          name: 'Persist Test',
          phone: '555-1111',
        },
        vehicle: {
          year: 2020,
          make: 'Test',
          model: 'Test',
        },
        services: ['Test'],
      }

      const created = createRepairOrder(newOrder)
      const retrieved = getRepairOrderById(created.id)

      expect(retrieved).toEqual(created)
    })
  })

  describe('updateRepairOrder', () => {
    it('should update status', () => {
      const updated = updateRepairOrder('RO-1', { status: 'AWAITING_APPROVAL' })

      expect(updated).toBeDefined()
      expect(updated?.status).toBe('AWAITING_APPROVAL')
    })

    it('should update assigned technician', () => {
      const updated = updateRepairOrder('RO-1', {
        assignedTech: {
          id: 'tech-2',
          name: 'Jane Smith',
          initials: 'JS',
          specialties: [],
          active: true,
        },
      })

      expect(updated).toBeDefined()
      expect(updated?.assignedTech?.id).toBe('tech-2')
      expect(updated?.assignedTech?.name).toBe('Jane Smith')
    })

    it('should update notes', () => {
      const updated = updateRepairOrder('RO-1', { notes: 'Updated notes' })

      expect(updated).toBeDefined()
      expect(updated?.notes).toBe('Updated notes')
    })

    it('should update approved_by_customer', () => {
      const updated = updateRepairOrder('RO-1', { approvedByCustomer: true })

      expect(updated).toBeDefined()
      expect(updated?.approvedByCustomer).toBe(true)
    })

    it('should handle multiple field updates', () => {
      const updated = updateRepairOrder('RO-1', {
        status: 'IN_PROGRESS',
        assignedTech: {
          id: 'tech-1',
          name: 'John Doe',
          initials: 'JD',
          specialties: [],
          active: true,
        },
        notes: 'Work started',
      })

      expect(updated).toBeDefined()
      expect(updated?.status).toBe('IN_PROGRESS')
      expect(updated?.assignedTech?.id).toBe('tech-1')
      expect(updated?.notes).toBe('Work started')
    })

    it('should return null when order not found', () => {
      const updated = updateRepairOrder('NON-EXISTENT', { status: 'IN_PROGRESS' })
      expect(updated).toBeNull()
    })

    it('should return existing order when no updates provided', () => {
      const original = getRepairOrderById('RO-1')
      const updated = updateRepairOrder('RO-1', {})

      expect(updated).toEqual(original)
    })

    it('should unassign technician when set to null', () => {
      const updated = updateRepairOrder('RO-2', { assignedTech: null })

      expect(updated).toBeDefined()
      expect(updated?.assignedTech).toBeNull()
    })
  })

  describe('deleteRepairOrder', () => {
    it('should delete existing order and return true', () => {
      const result = deleteRepairOrder('RO-1')

      expect(result).toBe(true)
      expect(getRepairOrderById('RO-1')).toBeNull()
    })

    it('should return false when order not found', () => {
      const result = deleteRepairOrder('NON-EXISTENT')
      expect(result).toBe(false)
    })

    it('should actually remove order from database', () => {
      const beforeCount = getAllRepairOrders().length

      deleteRepairOrder('RO-1')

      const afterCount = getAllRepairOrders().length
      expect(afterCount).toBe(beforeCount - 1)
    })
  })
})
