/**
 * Generates a test user object with default values and a randomized email.
 * Environment variables can override default fields to allow customization.
 *
 * @returns A user object with form data for account creation or login tests.
 */
export function generateTestUser() {
    const random = Math.floor(Math.random() * 100000);

    return {
        name: process.env.TEST_NAME ?? 'QA User',
        email: `${random}@example.com`,
        title: process.env.TEST_TITLE ?? 'Mr.',
        password: process.env.TEST_PASSWORD ?? 'Secure@123',
        birth_day: process.env.TEST_BIRTH_DAY ?? '15',
        birth_month: process.env.TEST_BIRTH_MONTH ?? '4',
        birth_year: process.env.TEST_BIRTH_YEAR ?? '1985',
        firstname: process.env.TEST_FIRST_NAME ?? 'QA',
        lastname: process.env.TEST_LAST_NAME ?? 'User',
        company: process.env.TEST_COMPANY ?? 'New Company',
        address1: process.env.TEST_ADDRESS1 ?? '123 Main Street',
        address2: process.env.TEST_ADDRESS2 ?? 'Suite 456',
        country: process.env.TEST_COUNTRY ?? 'United States',
        zipcode: process.env.TEST_ZIPCODE ?? '10018',
        state: process.env.TEST_STATE ?? 'New York',
        city: process.env.TEST_CITY ?? 'New York',
        mobile_number: process.env.TEST_MOBILE_NUMBER ?? '5551234567',
    };
}