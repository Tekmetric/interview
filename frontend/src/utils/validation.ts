import { z } from 'zod';

// Zod schema for user form validation
export const userFormSchema = z.object({
  name: z
    .string()
    .min(1, 'Name is required')
    .min(2, 'Name must be at least 2 characters long')
    .max(100, 'Name must be less than 100 characters'),

  email: z
    .string()
    .min(1, 'Email is required')
    .email('Please enter a valid email address')
    .max(254, 'Email must be less than 254 characters'),

  phone: z
    .string()
    .optional()
    .refine(
      val => !val || /^[+]?[\d\s().-]{10,}$/.test(val),
      'Please enter a valid phone number (minimum 10 digits)'
    ),

  company: z
    .string()
    .optional()
    .refine(
      val => !val || val.trim().length >= 2,
      'Company name must be at least 2 characters long'
    ),

  status: z.enum(['Active', 'Inactive'], {
    message: 'Status must be either Active or Inactive',
  }),
});

// TypeScript type inferred from the schema
export type UserFormData = z.infer<typeof userFormSchema>;

// Validation result type for React Query error handling
export interface ValidationError {
  field: string;
  message: string;
}

// Helper function to convert Zod errors to a more usable format
export const formatZodErrors = (error: z.ZodError): Record<string, string> => {
  const formattedErrors: Record<string, string> = {};

  error.issues.forEach(issue => {
    const field = issue.path.join('.');
    formattedErrors[field] = issue.message;
  });

  return formattedErrors;
};

// Validate a single field (useful for real-time validation)
export const validateField = (
  fieldName: keyof UserFormData,
  value: UserFormData[keyof UserFormData]
): string => {
  try {
    const fieldSchema = userFormSchema.shape[fieldName];
    fieldSchema.parse(value);
    return '';
  } catch (error) {
    if (error instanceof z.ZodError) {
      return error.issues[0]?.message || 'Invalid value';
    }
    return 'Validation error';
  }
};

// Validate the entire form
export const validateUserForm = (data: Partial<UserFormData>) => {
  try {
    userFormSchema.parse(data);
    return { isValid: true, errors: {} };
  } catch (error) {
    if (error instanceof z.ZodError) {
      return {
        isValid: false,
        errors: formatZodErrors(error),
      };
    }
    return {
      isValid: false,
      errors: { form: 'Validation failed' },
    };
  }
};
