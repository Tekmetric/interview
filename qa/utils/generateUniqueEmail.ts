/**
 * Generate a unique email.
 *
 * @returns {string} Unique email address based on the current timestamp.
 */
export const generateUniqueEmail = (): string => {
  return `john.doe.${Date.now()}@example.com`;
};