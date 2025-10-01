import type { Request, Response, NextFunction } from 'express'
import { z } from 'zod'

/**
 * Validation middleware factory
 * Validates request body, query, or params against a Zod schema
 */
export function validate(
  schema: z.ZodSchema,
  source: 'body' | 'query' | 'params' = 'body',
) {
  return (req: Request, res: Response, next: NextFunction) => {
    try {
      const data = req[source]
      const validated = schema.parse(data)

      // Replace request data with validated data
      req[source] = validated

      next()
    } catch (error) {
      if (error instanceof z.ZodError) {
        return res.status(400).json({
          error: 'VALIDATION_ERRORq',
          message: 'Invalid request data',
          details: error.issues.map((err) => ({
            path: err.path.join('.'),
            message: err.message,
          })),
        })
      }

      return res.status(500).json({
        error: 'INTERNAL_ERROR',
        message: 'An unexpected error occurred during validation',
      })
    }
  }
}
