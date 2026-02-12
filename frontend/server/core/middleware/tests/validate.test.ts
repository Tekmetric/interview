import { describe, it, expect, vi } from 'vitest'
import { z } from 'zod'
import { validate } from '../validate'
import type { Request, Response } from 'express'

describe('validate middleware', () => {
  describe('body validation', () => {
    it('should pass valid data through unchanged', () => {
      const schema = z.object({
        name: z.string(),
        age: z.number(),
      })

      const req = {
        body: { name: 'John', age: 30 },
      } as Request

      const res = {} as Response
      const next = vi.fn()

      const middleware = validate(schema, 'body')
      middleware(req, res, next)

      expect(req.body).toEqual({ name: 'John', age: 30 })
      expect(next).toHaveBeenCalledOnce()
    })

    it('should return 400 for invalid data with proper error format', () => {
      const schema = z.object({
        name: z.string(),
        age: z.number(),
      })

      const req = {
        body: { name: 'John', age: 'invalid' },
      } as Request

      const json = vi.fn()
      const res = {
        status: vi.fn().mockReturnValue({ json }),
      } as unknown as Response

      const next = vi.fn()

      const middleware = validate(schema, 'body')
      middleware(req, res, next)

      expect(res.status).toHaveBeenCalledWith(400)
      expect(json).toHaveBeenCalledWith({
        error: 'VALIDATION_ERROR',
        message: 'Invalid request data',
        details: expect.arrayContaining([
          expect.objectContaining({
            path: 'age',
            message: expect.any(String),
          }),
        ]),
      })
      expect(next).not.toHaveBeenCalled()
    })

    it('should handle missing required fields', () => {
      const schema = z.object({
        name: z.string(),
        email: z.string().email(),
      })

      const req = {
        body: { name: 'John' },
      } as Request

      const json = vi.fn()
      const res = {
        status: vi.fn().mockReturnValue({ json }),
      } as unknown as Response

      const next = vi.fn()

      const middleware = validate(schema, 'body')
      middleware(req, res, next)

      expect(res.status).toHaveBeenCalledWith(400)
      expect(json).toHaveBeenCalledWith({
        error: 'VALIDATION_ERROR',
        message: 'Invalid request data',
        details: expect.arrayContaining([
          expect.objectContaining({
            path: 'email',
          }),
        ]),
      })
    })

    it('should handle multiple validation errors', () => {
      const schema = z.object({
        name: z.string().min(3),
        age: z.number().positive(),
        email: z.string().email(),
      })

      const req = {
        body: { name: 'AB', age: -5, email: 'invalid' },
      } as Request

      const json = vi.fn()
      const res = {
        status: vi.fn().mockReturnValue({ json }),
      } as unknown as Response

      const next = vi.fn()

      const middleware = validate(schema, 'body')
      middleware(req, res, next)

      expect(res.status).toHaveBeenCalledWith(400)
      const call = json.mock.calls[0][0]
      expect(call.details).toHaveLength(3)
      expect(call.details.map((d: any) => d.path)).toContain('name')
      expect(call.details.map((d: any) => d.path)).toContain('age')
      expect(call.details.map((d: any) => d.path)).toContain('email')
    })
  })

  describe('query validation', () => {
    it('should validate query parameters', () => {
      const schema = z.object({
        page: z.string().optional(),
        limit: z.string().optional(),
      })

      const req = {
        query: { page: '1', limit: '10' },
      } as unknown as Request

      const res = {} as Response
      const next = vi.fn()

      const middleware = validate(schema, 'query')
      middleware(req, res, next)

      expect(req.query).toEqual({ page: '1', limit: '10' })
      expect(next).toHaveBeenCalledOnce()
    })

    it('should return 400 for invalid query parameters', () => {
      const schema = z.object({
        status: z.enum(['active', 'inactive']),
      })

      const req = {
        query: { status: 'invalid' },
      } as unknown as Request

      const json = vi.fn()
      const res = {
        status: vi.fn().mockReturnValue({ json }),
      } as unknown as Response

      const next = vi.fn()

      const middleware = validate(schema, 'query')
      middleware(req, res, next)

      expect(res.status).toHaveBeenCalledWith(400)
      expect(json).toHaveBeenCalledWith({
        error: 'VALIDATION_ERROR',
        message: 'Invalid request data',
        details: expect.any(Array),
      })
    })
  })

  describe('params validation', () => {
    it('should validate route parameters', () => {
      const schema = z.object({
        id: z.string().uuid(),
      })

      const req = {
        params: { id: '123e4567-e89b-12d3-a456-426614174000' },
      } as unknown as Request

      const res = {} as Response
      const next = vi.fn()

      const middleware = validate(schema, 'params')
      middleware(req, res, next)

      expect(req.params).toEqual({ id: '123e4567-e89b-12d3-a456-426614174000' })
      expect(next).toHaveBeenCalledOnce()
    })

    it('should return 400 for invalid params', () => {
      const schema = z.object({
        id: z.string().uuid(),
      })

      const req = {
        params: { id: 'not-a-uuid' },
      } as unknown as Request

      const json = vi.fn()
      const res = {
        status: vi.fn().mockReturnValue({ json }),
      } as unknown as Response

      const next = vi.fn()

      const middleware = validate(schema, 'params')
      middleware(req, res, next)

      expect(res.status).toHaveBeenCalledWith(400)
    })
  })

  describe('edge cases', () => {
    it('should use body as default source', () => {
      const schema = z.object({
        value: z.string(),
      })

      const req = {
        body: { value: 'test' },
      } as Request

      const res = {} as Response
      const next = vi.fn()

      const middleware = validate(schema)
      middleware(req, res, next)

      expect(next).toHaveBeenCalledOnce()
    })

    it('should handle nested object validation', () => {
      const schema = z.object({
        user: z.object({
          name: z.string(),
          address: z.object({
            city: z.string(),
            zip: z.string(),
          }),
        }),
      })

      const req = {
        body: {
          user: {
            name: 'John',
            address: {
              city: 'NYC',
              zip: '10001',
            },
          },
        },
      } as Request

      const res = {} as Response
      const next = vi.fn()

      const middleware = validate(schema, 'body')
      middleware(req, res, next)

      expect(next).toHaveBeenCalledOnce()
    })

    it('should provide correct nested path in error details', () => {
      const schema = z.object({
        user: z.object({
          address: z.object({
            zip: z.string().length(5),
          }),
        }),
      })

      const req = {
        body: {
          user: {
            address: {
              zip: '123',
            },
          },
        },
      } as Request

      const json = vi.fn()
      const res = {
        status: vi.fn().mockReturnValue({ json }),
      } as unknown as Response

      const next = vi.fn()

      const middleware = validate(schema, 'body')
      middleware(req, res, next)

      expect(json).toHaveBeenCalledWith({
        error: 'VALIDATION_ERROR',
        message: 'Invalid request data',
        details: expect.arrayContaining([
          expect.objectContaining({
            path: 'user.address.zip',
          }),
        ]),
      })
    })
  })
})
