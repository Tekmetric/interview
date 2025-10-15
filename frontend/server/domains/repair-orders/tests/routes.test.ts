import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import express, { type Express } from 'express'
import request from 'supertest'
import Database from 'better-sqlite3'
import {
  createTestDb,
  cleanupDb,
  seedTestTechnicians,
  seedTestRepairOrders,
} from '@server/tests/test-utils'

// Mock the db import - shared across both imports
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

// Import router after mock is set up (this also imports transforms which imports technician repository)
const repairOrderRoutes = await import('../routes')

describe('Repair Order Routes', () => {
  let app: Express

  beforeEach(() => {
    mockDb.current = createTestDb()
    app = express()
    app.use(express.json())
    app.use('/api', repairOrderRoutes.default)
    seedTestTechnicians(mockDb.current!)
    seedTestRepairOrders(mockDb.current!)
  })

  afterEach(() => {
    if (mockDb.current) {
      cleanupDb(mockDb.current)
      mockDb.current = null
    }
  })

  describe('GET /api/repairOrders/overdue', () => {
    beforeEach(() => {
      vi.useFakeTimers()
    })

    afterEach(() => {
      vi.useRealTimers()
    })

    it('should return overdue orders', async () => {
      const now = new Date('2024-01-03T12:00:00Z')
      vi.setSystemTime(now)

      const response = await request(app).get('/api/repairOrders/overdue')

      expect(response.status).toBe(200)
      expect(response.body).toHaveLength(1)
      expect(response.body[0].id).toBe('RO-2')
    })
  })

  describe('GET /api/repairOrders/recent', () => {
    it('should return recent orders in descending order of creation', async () => {
      mockDb.current!.exec('DELETE FROM repair_orders')
      mockDb
        .current!.prepare(
          "INSERT INTO repair_orders (id, status, created_at, customer_name, customer_phone, vehicle_year, vehicle_make, vehicle_model, services) VALUES ('RO-OLD', 'NEW', '2023-01-01 10:00:00', 'a','a',2020,'a','a','[]')",
        )
        .run()
      mockDb
        .current!.prepare(
          "INSERT INTO repair_orders (id, status, created_at, customer_name, customer_phone, vehicle_year, vehicle_make, vehicle_model, services) VALUES ('RO-NEW', 'NEW', '2023-01-02 10:00:00', 'a','a',2020,'a','a','[]')",
        )
        .run()

      const response = await request(app).get('/api/repairOrders/recent?limit=2')

      expect(response.status).toBe(200)
      expect(response.body).toHaveLength(2)
      expect(response.body[0].id).toBe('RO-NEW')
      expect(response.body[1].id).toBe('RO-OLD')
    })

    it('should respect the limit query parameter', async () => {
      const response = await request(app).get('/api/repairOrders/recent?limit=1')

      expect(response.status).toBe(200)
      expect(response.body).toHaveLength(1)
    })
  })

  describe('GET /api/repairOrders', () => {
    it('should return all repair orders', async () => {
      const response = await request(app).get('/api/repairOrders')

      expect(response.status).toBe(200)
      expect(response.body).toHaveLength(2)
    })

    it('should filter by status', async () => {
      const response = await request(app).get('/api/repairOrders?status=NEW')

      expect(response.status).toBe(200)
      expect(response.body).toHaveLength(1)
      expect(response.body[0].status).toBe('NEW')
    })

    it('should filter by tech', async () => {
      const response = await request(app).get('/api/repairOrders?tech=tech-1')

      expect(response.status).toBe(200)
      expect(response.body).toHaveLength(1)
      expect(response.body[0].assignedTech.id).toBe('tech-1')
    })

    it('should filter by priority', async () => {
      const response = await request(app).get('/api/repairOrders?priority=HIGH')

      expect(response.status).toBe(200)
      expect(response.body).toHaveLength(1)
      expect(response.body[0].priority).toBe('HIGH')
    })

    it('should filter by search on customer name', async () => {
      const response = await request(app).get('/api/repairOrders?search=alice')

      expect(response.status).toBe(200)
      expect(response.body).toHaveLength(1)
      expect(response.body[0].customer.name).toBe('Alice Customer')
    })

    it('should filter by search on vehicle', async () => {
      const response = await request(app).get('/api/repairOrders?search=camry')

      expect(response.status).toBe(200)
      expect(response.body).toHaveLength(1)
      expect(response.body[0].vehicle.model).toBe('Camry')
    })

    it('should filter by search on plate', async () => {
      const response = await request(app).get('/api/repairOrders?search=xyz')

      expect(response.status).toBe(200)
      expect(response.body).toHaveLength(1)
      expect(response.body[0].vehicle.plate).toBe('XYZ789')
    })

    it('should return empty array when no matches', async () => {
      const response = await request(app).get('/api/repairOrders?status=COMPLETED')

      expect(response.status).toBe(200)
      expect(response.body).toEqual([])
    })

    it('should return 400 for invalid status filter', async () => {
      const response = await request(app).get('/api/repairOrders?status=INVALID')

      expect(response.status).toBe(400)
    })
  })

  describe('GET /api/repairOrders/:id', () => {
    it('should return repair order by id', async () => {
      const response = await request(app).get('/api/repairOrders/RO-1')

      expect(response.status).toBe(200)
      expect(response.body.id).toBe('RO-1')
      expect(response.body.customer.name).toBe('Alice Customer')
    })

    it('should return 404 when order not found', async () => {
      const response = await request(app).get('/api/repairOrders/RO-999')

      expect(response.status).toBe(404)
      expect(response.body).toHaveProperty('error')
    })
  })

  describe('POST /api/repairOrders', () => {
    it('should create new repair order', async () => {
      const newOrder = {
        customer: {
          name: 'Charlie Customer',
          phone: '555-9999',
        },
        vehicle: {
          year: 2021,
          make: 'Ford',
          model: 'F-150',
          mileage: 30000,
        },
        services: ['Oil Change'],
      }

      const response = await request(app).post('/api/repairOrders').send(newOrder)

      expect(response.status).toBe(201)
      expect(response.body).toHaveProperty('id')
      expect(response.body.customer.name).toBe('Charlie Customer')
      expect(response.body.status).toBe('NEW')
    })

    it('should return 400 for invalid data', async () => {
      const invalidOrder = {
        customer: {
          name: 'Charlie Customer',
        },
        // Missing required fields
      }

      const response = await request(app).post('/api/repairOrders').send(invalidOrder)

      expect(response.status).toBe(400)
    })
  })

  describe('PATCH /api/repairOrders/:id', () => {
    it('should update repair order', async () => {
      const updates = {
        priority: 'HIGH',
        notes: 'Updated notes',
      }

      const response = await request(app).patch('/api/repairOrders/RO-1').send(updates)

      expect(response.status).toBe(200)
      expect(response.body.priority).toBe('HIGH')
      expect(response.body.notes).toBe('Updated notes')
    })

    it('should allow valid status transition', async () => {
      const response = await request(app).patch('/api/repairOrders/RO-1').send({
        status: 'AWAITING_APPROVAL',
      })

      expect(response.status).toBe(200)
      expect(response.body.status).toBe('AWAITING_APPROVAL')
    })

    it('should reject invalid status transition', async () => {
      const response = await request(app).patch('/api/repairOrders/RO-1').send({
        status: 'COMPLETED',
      })

      expect(response.status).toBe(409)
      expect(response.body.error).toBe('INVALID_TRANSITION')
      expect(response.body).toHaveProperty('allowed')
    })

    it('should return 404 when order not found', async () => {
      const response = await request(app).patch('/api/repairOrders/RO-999').send({
        priority: 'HIGH',
      })

      expect(response.status).toBe(404)
    })

    it('should return 400 for invalid update data', async () => {
      const response = await request(app).patch('/api/repairOrders/RO-1').send({
        status: 'INVALID_STATUS',
      })

      expect(response.status).toBe(400)
    })
  })

  describe('DELETE /api/repairOrders/:id', () => {
    it('should delete repair order', async () => {
      const response = await request(app).delete('/api/repairOrders/RO-1')

      expect(response.status).toBe(204)

      // Verify deletion
      const getResponse = await request(app).get('/api/repairOrders/RO-1')
      expect(getResponse.status).toBe(404)
    })

    it('should return 404 when order not found', async () => {
      const response = await request(app).delete('/api/repairOrders/RO-999')

      expect(response.status).toBe(404)
    })
  })

  describe('Bulk Operations', () => {
    beforeEach(() => {
      // Seed additional orders for bulk testing
      mockDb
        .current!.prepare(
          "INSERT INTO repair_orders (id, status, created_at, customer_name, customer_phone, vehicle_year, vehicle_make, vehicle_model, services) VALUES ('RO-BULK-1', 'NEW', '2024-01-01 10:00:00', 'Test 1','555-0001',2020,'Honda','Civic','[]')",
        )
        .run()
      mockDb
        .current!.prepare(
          "INSERT INTO repair_orders (id, status, created_at, customer_name, customer_phone, vehicle_year, vehicle_make, vehicle_model, services) VALUES ('RO-BULK-2', 'NEW', '2024-01-01 10:00:00', 'Test 2','555-0002',2020,'Toyota','Corolla','[]')",
        )
        .run()
      mockDb
        .current!.prepare(
          "INSERT INTO repair_orders (id, status, created_at, customer_name, customer_phone, vehicle_year, vehicle_make, vehicle_model, services) VALUES ('RO-BULK-3', 'NEW', '2024-01-01 10:00:00', 'Test 3','555-0003',2020,'Ford','Focus','[]')",
        )
        .run()
    })

    it('should handle concurrent technician assignments', async () => {
      const orderIds = ['RO-BULK-1', 'RO-BULK-2', 'RO-BULK-3']
      const techId = 'tech-1'

      // Simulate bulk operation: concurrent PATCH requests
      const requests = orderIds.map((orderId) =>
        request(app).patch(`/api/repairOrders/${orderId}`).send({
          assignedTech: { id: techId },
        }),
      )

      const responses = await Promise.all(requests)

      // All requests should succeed
      responses.forEach((response) => {
        expect(response.status).toBe(200)
        expect(response.body.assignedTech.id).toBe(techId)
      })

      // Verify all orders were updated in database
      const verifyRequests = orderIds.map((orderId) =>
        request(app).get(`/api/repairOrders/${orderId}`),
      )
      const verifyResponses = await Promise.all(verifyRequests)

      verifyResponses.forEach((response) => {
        expect(response.status).toBe(200)
        expect(response.body.assignedTech.id).toBe(techId)
      })
    })

    it('should handle partial failures in bulk operations', async () => {
      const requests = [
        request(app).patch('/api/repairOrders/RO-BULK-1').send({ priority: 'HIGH' }),
        request(app).patch('/api/repairOrders/RO-BULK-2').send({ priority: 'HIGH' }),
        request(app).patch('/api/repairOrders/RO-INVALID').send({ priority: 'HIGH' }), // Should fail
      ]

      const responses = await Promise.allSettled(
        requests.map((r) => r.then((res) => ({ status: res.status, body: res.body }))),
      )

      // First two should succeed
      expect(responses[0].status).toBe('fulfilled')
      expect(responses[1].status).toBe('fulfilled')

      // Third should fail (404)
      expect(responses[2].status).toBe('fulfilled')
      if (responses[2].status === 'fulfilled') {
        expect(responses[2].value.status).toBe(404)
      }
    })
  })

})
