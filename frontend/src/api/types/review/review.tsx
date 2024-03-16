import { z } from 'zod'

const UserInfoSchema = z.object({
  id: z.string().uuid(),
  name: z.string(),
  avatar: z.string().url(),
})

const ReviewSchema = z.object({
  createdAt: z.number(),
  updatedAt: z.string(),
  id: z.string().uuid(),
  rating: z.number().int(),
  message: z.string().url(),
  userInfo: UserInfoSchema,
})

type Review = z.infer<typeof ReviewSchema>
type UserInfo = z.infer<typeof UserInfoSchema>

export type { Review, UserInfo }

export { ReviewSchema, UserInfoSchema }
