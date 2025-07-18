import { test, request } from '@playwright/test';
import { LoginPage } from '../../pages/login.page';
import { UserApi } from '../../api/user.api';
import { generateTestUser } from '../../utils/test-users';

/**
 * Test suite to automate login UI with API-generated user credentials.
 * Ensures login functionality works with valid and invalid inputs.
 */
test.describe('Automate Login UI', () => {
    let userApi: UserApi;
    let loginPage: LoginPage;
    let user: ReturnType<typeof generateTestUser>;

    test.beforeEach(async ({ page }) => {
        // Initialize API and UI contexts
        const requestContext = await request.newContext();
        userApi = new UserApi(requestContext, process.env.BASE_URL!);
        loginPage = new LoginPage(page);
        user = generateTestUser();

        // Create a new user via API for the test
        await userApi.createUser(user);
        // Navigate to login page
        await loginPage.navigate();
    });

    test.afterEach(async () => {
        // Clean up by deleting the test user
        await userApi.deleteUser(user.email, user.password);
    });

    test('Should log in successfully with valid credentials', async () => {
        // Perform login with API-generated credentials
        await loginPage.login(user.email, user.password);
        // Verify successful login by checking the user's name
        await loginPage.verifyLoginSuccess(user.name);
    });

    test('Should display error for invalid login credentials', async () => {
        // Attempt login with a randomly generated invalid email
        const random = Math.floor(Math.random() * 100000);
        await loginPage.login(`${random}@invalid.com`, 'WrongPassword');
        // Verify that an error message is displayed
        await loginPage.verifyLoginFailure();
    });
});