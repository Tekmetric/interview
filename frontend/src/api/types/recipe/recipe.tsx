import { z } from "zod";

const MealTypes = [
  "BREAKFAST",
  "LUNCH",
  "SALAD",
  "SOUP",
  "BREAD",
  "DESSERT",
] as const;
const MealTypesSchema = z.enum(MealTypes);

const UserInfoSchema = z.object({
  id: z.string().uuid(),
  name: z.string(),
  avatar: z.string().url(),
});

const RecipeSchema = z.object({
  createdAt: z.number(),
  updatedAt: z.string(),
  id: z.string().uuid(),
  image: z.string().url(),
  title: z.string(),
  description: z.string(),
  mealType: MealTypesSchema,
  duration: z.number().int(),
  ratingAverage: z.number(),
  ratingCount: z.number().int(),
  userInfo: UserInfoSchema,
});

type Recipe = z.infer<typeof RecipeSchema>;
type UserInfo = z.infer<typeof UserInfoSchema>;
type MealType = z.infer<typeof MealTypesSchema>;

export type { MealType, Recipe, UserInfo };

export { MealTypes, MealTypesSchema, RecipeSchema, UserInfoSchema };
