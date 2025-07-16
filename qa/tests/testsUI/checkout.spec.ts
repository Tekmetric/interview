// testsUI/checkout.spec.ts
import { test, expect } from '@playwright/test';
import { CheckoutPage } from '../pageObjects/CheckoutPage';
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
  let productName: string;
  const searchTerm = 'Tshirt'; // Search for this item

  test.beforeAll(async ({ playwright }) => {
    apiRequestContext = await playwright.request.newContext();
    userApi = new UserAccountAPI(apiRequestContext, 'https://automationexercise.com/api');
    productApi = new ProductAPI(apiRequestContext, 'https://automationexercise.com/api');

    email = generateUniqueEmail();

    const createUserResponse = await userApi.createUser(email, password, { ...userData, email });
    const createUserResponseData = await createUserResponse.json();
    console.log('API Setup: Create user response:', createUserResponseData);

    const searchResponse = await productApi.searchProduct(searchTerm);
    const responseData = await searchResponse.json();
    console.log('Full API Response JSON:', JSON.stringify(responseData, null, 2));

    if (responseData.products && responseData.products.length > 0) {
      productName = responseData.products[0].name;
      console.log('Product found:', productName);

    } else {
      console.error('No products found for the search term:', searchTerm);
      throw new Error('No products found in search.');
    }
  });

  test('should complete the checkout process', async ({ page }) => {
    const checkoutPage = new CheckoutPage(page);

    await checkoutPage.navigateToHomePage();
    await checkoutPage.login(email, password);
    await checkoutPage.goToCart();

    // Navigate to products page with the search category
    await page.goto(`https://www.automationexercise.com/products?search=${searchTerm}`);

    // Hover over the first item in the list
    const firstItem = page.locator('.productinfo').first();
    await firstItem.scrollIntoViewIfNeeded();
    await firstItem.hover(); // Perform the hover action


    await firstItem.click();

    await checkoutPage.addFirstItemToCart();
    await checkoutPage.proceedToCheckout();
    await checkoutPage.placeOrder();
    await checkoutPage.fillCardDetails(userData.cardDetails);
    await checkoutPage.confirmOrder();
    await checkoutPage.expectOrderConfirmation();
  });

  test.afterAll(async () => {
    const deleteUserResponse = await userApi.deleteUser(email, password);
    const deleteUserResponseData = await deleteUserResponse.json();
    console.log('API Cleanup: Delete user response:', deleteUserResponseData);

    await apiRequestContext.dispose();
  });
});