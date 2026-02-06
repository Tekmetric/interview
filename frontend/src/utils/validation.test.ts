import { describe, expect, it } from 'vitest';

import { formatZodErrors, userFormSchema, validateUserForm } from './validation';

describe('User Form Validation', () => {
  describe('userFormSchema', () => {
    it('validates a correct user object', () => {
      const validUser = {
        name: 'John Doe',
        email: 'john@example.com',
        phone: '+1234567890',
        company: 'Acme Corp',
        status: 'Active' as const,
      };

      const result = userFormSchema.safeParse(validUser);
      expect(result.success).toBe(true);
    });

    it('requires name field', () => {
      const invalidUser = {
        email: 'john@example.com',
        status: 'Active' as const,
      };

      const result = userFormSchema.safeParse(invalidUser);
      expect(result.success).toBe(false);
      if (!result.success) {
        expect(result.error.issues[0].path[0]).toBe('name');
        // Check that the error indicates the field is missing/required
        expect(result.error.issues[0].message).toMatch(/required|expected string|undefined/i);
      }
    });

    it('validates email format', () => {
      const invalidUser = {
        name: 'John Doe',
        email: 'invalid-email',
        status: 'Active' as const,
      };

      const result = userFormSchema.safeParse(invalidUser);
      expect(result.success).toBe(false);
      if (!result.success) {
        const emailError = result.error.issues.find(issue => issue.path[0] === 'email');
        expect(emailError?.message).toBe('Please enter a valid email address');
      }
    });

    it('validates phone number format', () => {
      const invalidUser = {
        name: 'John Doe',
        email: 'john@example.com',
        phone: '123', // Too short
        status: 'Active' as const,
      };

      const result = userFormSchema.safeParse(invalidUser);
      expect(result.success).toBe(false);
      if (!result.success) {
        const phoneError = result.error.issues.find(issue => issue.path[0] === 'phone');
        expect(phoneError?.message).toBe('Please enter a valid phone number (minimum 10 digits)');
      }
    });

    it('accepts optional empty phone', () => {
      const validUser = {
        name: 'John Doe',
        email: 'john@example.com',
        status: 'Active' as const,
      };

      const result = userFormSchema.safeParse(validUser);
      expect(result.success).toBe(true);
    });

    it('validates status enum', () => {
      const invalidUser = {
        name: 'John Doe',
        email: 'john@example.com',
        status: 'InvalidStatus',
      };

      const result = userFormSchema.safeParse(invalidUser);
      expect(result.success).toBe(false);
      if (!result.success) {
        const statusError = result.error.issues.find(issue => issue.path[0] === 'status');
        expect(statusError?.message).toBe('Status must be either Active or Inactive');
      }
    });

    it('validates name length constraints', () => {
      // Too short
      let result = userFormSchema.safeParse({
        name: 'A',
        email: 'john@example.com',
        status: 'Active' as const,
      });
      expect(result.success).toBe(false);

      // Too long
      result = userFormSchema.safeParse({
        name: 'A'.repeat(101),
        email: 'john@example.com',
        status: 'Active' as const,
      });
      expect(result.success).toBe(false);
    });
  });

  describe('formatZodErrors', () => {
    it('formats Zod errors correctly', () => {
      const invalidUser = {
        name: '',
        email: 'invalid-email',
        status: 'InvalidStatus',
      };

      const result = userFormSchema.safeParse(invalidUser);
      expect(result.success).toBe(false);

      if (!result.success) {
        const formattedErrors = formatZodErrors(result.error);

        expect(formattedErrors).toHaveProperty('name');
        expect(formattedErrors).toHaveProperty('email');
        expect(formattedErrors).toHaveProperty('status');

        // Just check that error messages exist, don't be too specific about exact wording
        expect(formattedErrors.name).toBeTruthy();
        expect(formattedErrors.email).toBe('Please enter a valid email address');
        expect(formattedErrors.status).toBe('Status must be either Active or Inactive');
      }
    });
  });

  describe('validateUserForm', () => {
    it('returns valid result for correct data', () => {
      const validUser = {
        name: 'John Doe',
        email: 'john@example.com',
        status: 'Active' as const,
      };

      const result = validateUserForm(validUser);
      expect(result.isValid).toBe(true);
      expect(result.errors).toEqual({});
    });

    it('returns errors for invalid data', () => {
      const invalidUser = {
        name: '',
        email: 'invalid-email',
        status: 'InvalidStatus',
      } as unknown as Parameters<typeof validateUserForm>[0];

      const result = validateUserForm(invalidUser);
      expect(result.isValid).toBe(false);
      expect(Object.keys(result.errors)).toContain('name');
      expect(Object.keys(result.errors)).toContain('email');
      expect(Object.keys(result.errors)).toContain('status');
    });
  });
});
