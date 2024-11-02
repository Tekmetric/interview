import { z } from 'zod'

export const createQuestionValidationSchema = z.object({
  title: z.string().min(5, 'Title must have at least 5 characters'),
  description: z.string().min(5, 'Description must have at least 5 characters')
})
