import { test, expect } from '@playwright/test';
import { CheckoutPage } from '../pageObjects/CheckoutPage';
import { LoginPage } from '../pageObjects/LoginPage';
import { UserAccountAPI } from '../pageObjects/UserAccountAPI';
import { ProductAPI } from '../pageObjects/ProductAPI';
import { generateUniqueEmail } from '../../utils/generateUniqueEmail';
import * as userData from '../../testData/userData.json';

test.describe('Checkout Process', () => {
  let email: string;
  const password = userData.password;
  let userApi: UserAccountAPI;
  let productApi: ProductAPI;
  let apiRequestContext: any;
  const searchTerm = 'Blue Top'; // Search for this item

  test.beforeAll(async ({ request, playwright }) => {
    // Initialize API contexts
    apiRequestContext = await playwright.request.newContext();
    userApi = new UserAccountAPI(apiRequestContext, 'https://automationexercise.com/api');
    productApi = new ProductAPI(apiRequestContext, 'https://automationexercise.com/api');

    // Generate a unique email
    email = generateUniqueEmail();

    // Create user for test
    const createUserResponse = await userApi.createUser(email, password, { ...userData, email });
    const createUserResponseData = await createUserResponse.json();
    console.log('API Setup: Create user response:', createUserResponseData);

});

  test('should complete the checkout process', async ({ page }) => {
    const checkoutPage = new CheckoutPage(page);
    const loginPage = new LoginPage(page);

    // Navigate to home and perform login
    await checkoutPage.navigateToHomePage();
    await loginPage.login(email, password);

    // Navigate to products page and find product by name
    await page.goto(`https://www.automationexercise.com/products?search=${searchTerm}`);

    await productApi.searchProduct(searchTerm);
    // Interact with product
    await checkoutPage.addProductToCart(searchTerm);

    // Proceed through checkout flow
    await checkoutPage.proceedToCheckout();
    await checkoutPage.placeOrder();
    await checkoutPage.fillCardDetails(userData.cardDetails);
    await checkoutPage.confirmOrder();
    await checkoutPage.expectOrderConfirmation();
  });

  test.afterAll(async () => {
    // Cleanup user
    const deleteUserResponse = await userApi.deleteUser(email, password);
    const deleteUserResponseData = await deleteUserResponse.json();
    console.log('API Cleanup: Delete user response:', deleteUserResponseData);

    await apiRequestContext.dispose();
  });
});
