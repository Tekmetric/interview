import { test, request } from '@playwright/test';
import { UserApi } from '../../api/user.api';
import { generateTestUser } from '../../utils/test-users';

/**
 * Test suite to automate login operations via API.
 * Verifies successful login with valid credentials and error handling for invalid credentials.
 */
test.describe('Automate Login API', () => {
    let userApi: UserApi;
    let user: ReturnType<typeof generateTestUser>;

    test.beforeEach(async () => {
        // Initialize API context and generate a new user
        const requestContext = await request.newContext();
        userApi = new UserApi(requestContext, process.env.BASE_URL!);
        user = generateTestUser();

        // Create a new user via API for the test
        await userApi.createUser(user);
    });

    test.afterEach(async () => {
        // Clean up by deleting the test user
        await userApi.deleteUser(user.email, user.password);
    });

    test('Should log in successfully with valid credentials', async () => {
        // Verify login with the created user's credentials
        await userApi.verifyUserExists(user.email, user.password);
    });

    test('Should display error for invalid login credentials', async () => {
        // Attempt login with a randomly generated invalid email
        const random = Math.floor(Math.random() * 100000);
        await userApi.verifyUserDoesNotExist(`${random}@invalid.com`, 'WrongPassword');
    });
});