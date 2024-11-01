import { z } from 'zod'

export const createAnswerValidationSchema = z.object({
  description: z.string().min(5, 'Description must have at least 5 characters')
})
