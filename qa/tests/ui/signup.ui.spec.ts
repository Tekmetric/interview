import { test, request } from '@playwright/test';
import { SignupPage } from '../../pages/signup.page';
import { UserApi } from '../../api/user.api';
import { generateTestUser } from '../../utils/test-users';

/**
 * Test suite to automate account creation UI with API support.
 * Ensures account creation works with valid user data.
 */
test.describe('Account Creation UI', () => {
    let userApi: UserApi;
    let signupPage: SignupPage;
    let user: ReturnType<typeof generateTestUser>;

    test.beforeEach(async ({ page }) => {
        // Initialize API and UI contexts
        const requestContext = await request.newContext();
        userApi = new UserApi(requestContext, process.env.BASE_URL!);
        signupPage = new SignupPage(page);
        user = generateTestUser();

        // Navigate to signup page
        await signupPage.navigate();
    });

    test.afterEach(async () => {
        // Clean up by deleting the test user
        await userApi.deleteUser(user.email, user.password);
    });

    test('Successful Account Creation with Valid Data', async () => {
        // Perform signup with API-generated user data
        await signupPage.signUp(user);
        // Verify that the account has been created
        await signupPage.verifyAccountCreated();
    });
});