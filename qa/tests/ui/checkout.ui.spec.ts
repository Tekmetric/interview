import { test, request } from '@playwright/test';
import { LoginPage } from '../../pages/login.page';
import { PlpPage } from '../../pages/plp.page';
import { PdpPage } from '../../pages/pdp.page';
import { CartPage } from '../../pages/cart.page';
import { CheckoutPage } from '../../pages/checkout.page';
import { UserApi } from '../../api/user.api';
import { generateTestUser } from '../../utils/test-users';

/**
 * Test suite to automate checkout UI with API-generated user credentials.
 * Ensures end-to-end checkout flow from product selection to order confirmation.
 */
test.describe('Checkout UI', () => {
    let userApi: UserApi;
    let loginPage: LoginPage;
    let plpPage: PlpPage;
    let pdpPage: PdpPage;
    let cartPage: CartPage;
    let checkoutPage: CheckoutPage;
    let user: ReturnType<typeof generateTestUser>;

    test.beforeEach(async ({ page }) => {
        // Initialize API and UI contexts
        const requestContext = await request.newContext();
        userApi = new UserApi(requestContext, process.env.BASE_URL!);

        // Generate and create a new user via API
        user = generateTestUser();
        await userApi.createUser(user);

        // Initialize page objects for the test flow
        loginPage = new LoginPage(page);
        plpPage = new PlpPage(page);
        pdpPage = new PdpPage(page);
        cartPage = new CartPage(page);
        checkoutPage = new CheckoutPage(page);

        // Log in with the created user
        await loginPage.navigate();
        await loginPage.login(user.email, user.password);
    });

    test.afterEach(async () => {
        // Clean up by deleting the test user
        await userApi.deleteUser(user.email, user.password);
    });

    test('Successful Checkout Flow (UI end-to-end)', async () => {
        // Navigate to product listing page and select a product
        await plpPage.navigate();
        await plpPage.selectProduct();

        // Add the selected product to the cart
        await pdpPage.addToCart();

        // Proceed to checkout from the cart page
        await cartPage.proceedToCheckout();

        // Complete the payment process
        await checkoutPage.proceedToPayment();
        await checkoutPage.fillPaymentDetails({
            name: process.env.TEST_CC_NAME,
            number: process.env.TEST_CC_NUMBER,
            cvc: process.env.TEST_CC_CVC,
            expMonth: process.env.TEST_CC_EXP_MONTH,
            expYear: process.env.TEST_CC_EXP_YEAR
        });
        await checkoutPage.confirmOrder();

        // Verify that the order was successfully placed
        await checkoutPage.verifyOrderSuccess();
    });
});