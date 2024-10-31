import { type ValidationErrors, setIn } from 'final-form'
import { ZodError, type ZodSchema, type z } from 'zod'

const validateFormValues = (
  values: object,
  schema: ZodSchema
): ValidationErrors | Promise<ValidationErrors> => {
  try {
    schema.parse(values)
  } catch (error) {
    if (error instanceof ZodError) {
      return error.issues.reduce((formError: object, issue: z.ZodIssue) => {
        return setIn(formError, issue.path.join('.'), issue.message)
      }, {})
    }

    throw error // rethrow the error if it's not a ZodError
  }
}

export const validateSchema =
  (schema: z.ZodSchema<object>) => (values: object) => {
    return validateFormValues(values, schema)
  }
