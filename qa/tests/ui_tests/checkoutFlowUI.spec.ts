import { test, expect } from '@playwright/test';
import { AccountPage } from '../../pages/AccountPage';
import { CartCheckoutPage } from '../../pages/CheckoutPage';
import { Homepage } from '../../pages/Homepage';
import { ProductPage } from '../../pages/ProductPage';
import { SignupPage } from '../../pages/SignupPage';

test('Complete Checkout - PDP', async ({ page }) => {
    const homePage = new Homepage(page);
    const accountPage = new AccountPage(page);
    const signupPage = new SignupPage(page)
    const productPage = new ProductPage(page);
    const checkoutFlow = new CartCheckoutPage(page);
    
    // Go to storefront & log in
    await homePage.goto();
    await homePage.goToSignupLogin();
    await accountPage.startSignUp();
    await signupPage.completeSignup();

    // Navigate to PLP
    await homePage.goToProducts();

    // Navigate to PDP of 1st product
    await expect(productPage.plpAdvertisement).toBeVisible();
    await productPage.viewFirstProductLink.click();

    // Add to cart
    await expect(productPage.commerceBlock).toBeVisible();
    await productPage.addToCart.click();

    // Wait for added to cart popup
    await expect(productPage.cartModal).toBeVisible();
    
    // Navigate to cart & check out
    await productPage.modalViewCartLink.click();
    await checkoutFlow.completeCheckout();

    // Wait for Order Confirmation to be visible
    await expect(checkoutFlow.orderPlacedConfirmation).toBeVisible();

    // Clean up account after use
    await homePage.deleteAccount();
});
