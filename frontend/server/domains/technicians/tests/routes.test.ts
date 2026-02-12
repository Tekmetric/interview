import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import express, { type Express } from 'express'
import request from 'supertest'
import Database from 'better-sqlite3'
import { createTestDb, cleanupDb, seedTestTechnicians } from '@server/tests/test-utils'

// Mock the db import
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

// Import router after mock is set up
const technicianRoutes = await import('../routes')

describe('Technician Routes', () => {
  let app: Express

  beforeEach(() => {
    mockDb.current = createTestDb()
    app = express()
    app.use(express.json())
    app.use('/api', technicianRoutes.default)
  })

  afterEach(() => {
    if (mockDb.current) {
      cleanupDb(mockDb.current)
      mockDb.current = null
    }
  })

  describe('GET /api/technicians', () => {
    it('should return all active technicians', async () => {
      seedTestTechnicians(mockDb.current!)

      const response = await request(app).get('/api/technicians')

      expect(response.status).toBe(200)
      expect(response.body).toHaveLength(2)
      expect(response.body.every((t: { active: boolean }) => t.active)).toBe(true)
    })

    it('should return empty array when no technicians exist', async () => {
      const response = await request(app).get('/api/technicians')

      expect(response.status).toBe(200)
      expect(response.body).toEqual([])
    })

    it('should return technicians with correct structure', async () => {
      seedTestTechnicians(mockDb.current!)

      const response = await request(app).get('/api/technicians')

      expect(response.status).toBe(200)
      const tech = response.body[0]
      expect(tech).toHaveProperty('id')
      expect(tech).toHaveProperty('name')
      expect(tech).toHaveProperty('initials')
      expect(tech).toHaveProperty('specialties')
      expect(tech).toHaveProperty('active')
    })

    it('should not include inactive technicians', async () => {
      seedTestTechnicians(mockDb.current!)

      const response = await request(app).get('/api/technicians')

      expect(response.status).toBe(200)
      expect(
        response.body.find((t: { name: string }) => t.name === 'Bob Inactive'),
      ).toBeUndefined()
    })

    it('should return JSON content type', async () => {
      const response = await request(app).get('/api/technicians')

      expect(response.headers['content-type']).toMatch(/json/)
    })
  })
})
