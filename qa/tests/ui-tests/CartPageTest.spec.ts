import { test, expect } from '@playwright/test';
import { CartPage } from '../../page/CartPage';
import { LoginPage } from '../../page/LoginPage';

test('Complete checkout flow', async ({ page }) => {
    const cartPage = new CartPage(page);
    const loginPage = new LoginPage(page);

    
    // Navigate to Login Page
    await loginPage.navigate();

    // Perform Login
    await loginPage.login('alan-test@test.com', 'SecurePassword123');


    // Add two random items to the cart
    await cartPage.addTwoRandomItemsToCart();

    // Ensure cart is not empty before proceeding
    await cartPage.viewCart();
    if (await page.locator('text=Cart is empty!').isVisible()) {
        throw new Error("Cannot proceed to checkout: Cart is empty!");
    }

    // Proceed To Checkout
    await cartPage.proceedToCheckout();

    // Add a comment
    await cartPage.addComment('This is a test order.');

    // Place order
    await cartPage.placeOrder();

    // Fill payment details and confirm order
    await cartPage.fillPaymentDetails('Test User', '4111111111111111', '123', '12', '2026');
    await cartPage.confirmPayment();
});
