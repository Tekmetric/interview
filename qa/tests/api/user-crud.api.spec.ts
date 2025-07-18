import { test, request } from '@playwright/test';
import { UserApi } from '../../api/user.api';
import { generateTestUser } from '../../utils/test-users';

/**
 * Test suite to automate CRUD operations for user accounts via API.
 * Verifies the full lifecycle of user creation, reading, updating, and deletion.
 */
test.describe('User Account CRUD API', () => {
    test('Performs full CRUD flow for a user via API', async () => {
        // Initialize API context and generate a new user
        const requestContext = await request.newContext();
        const userApi = new UserApi(requestContext, process.env.BASE_URL!);
        const user = generateTestUser();

        // Create a new user via API
        await userApi.createUser(user);

        // Read the user details to verify creation
        await userApi.readUser(user.email);

        // Update the user with new city information
        await userApi.updateUser(user.email, user.password, { city: 'Updated City' });

        // Delete the user to clean up
        await userApi.deleteUser(user.email, user.password);
    });
});