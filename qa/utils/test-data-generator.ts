import { faker } from "@faker-js/faker";

/**
 * Test Data Generator using Faker.js
 * Generates realistic, unique test data for user registration and login
 */

export interface UserData {
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  password: string;
  address: string;
  address2?: string;
  country: string;
  state: string;
  city: string;
  zipcode: string;
  mobileNumber: string;
  company?: string;
  title?: "Mr" | "Mrs";
  dateOfBirth?: {
    day: string;
    month: string;
    year: string;
  };
}

export interface MinimalUserData {
  fullName: string;
  email: string;
  password: string;
}

/**
 * Generate a unique email address
 * Uses faker + timestamp to ensure uniqueness across test runs
 */
export function generateUniqueEmail(
  domain: string = "automationtest.com"
): string {
  const uniqueId = Date.now();
  const username = faker.internet.username().toLowerCase();
  return `${username}_${uniqueId}@${domain}`;
}

/**
 * Generate a strong password
 * @param length - Password length (default: 12)
 */
export function generatePassword(length: number = 12): string {
  return faker.internet.password({
    length,
    memorable: false,
    pattern: /[A-Za-z0-9!@#$%]/,
    prefix: "Test@", // Ensures it meets common password requirements
  });
}

/**
 * Generate a mobile phone number
 * @returns
 */
export function generateMobileNumber(): string {
  const areaCode = faker.number.int({ min: 200, max: 999 });
  const prefix = faker.number.int({ min: 200, max: 999 });
  const lineNumber = faker.number.int({ min: 1000, max: 9999 });
  return `${areaCode}${prefix}${lineNumber}`;
}

/**
 * Generate complete user data with all required fields
 * Perfect for full registration flows
 */
export function generateUserData(overrides?: Partial<UserData>): UserData {
  const firstName = faker.person.firstName();
  const lastName = faker.person.lastName();

  return {
    firstName,
    lastName,
    fullName: `${firstName} ${lastName}`,
    email: generateUniqueEmail(),
    password: generatePassword(),
    address: faker.location.streetAddress(),
    country: "United States", // Default for automationexercise.com
    state: faker.location.state(),
    city: faker.location.city(),
    zipcode: faker.location.zipCode("#####"), // US format: 5 digits
    mobileNumber: generateMobileNumber(), // 10 digits
    ...overrides,
  };
}

/**
 * Generate invalid test data for negative testing
 */
export const InvalidTestData = {
  /**
   * Invalid email formats
   */
  emails: {
    missingAt: `testuser${Date.now()}.com`,
    missingDomain: `test${Date.now()}@`,
  },
};

/**
 * Generate login credentials for existing user tests
 */
export function generateLoginCredentials(overrides?: {
  email?: string;
  password?: string;
}) {
  return {
    email: overrides?.email || generateUniqueEmail(),
    password: overrides?.password || generatePassword(),
  };
}
