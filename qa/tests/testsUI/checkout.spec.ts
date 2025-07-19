import { test, expect } from '@playwright/test';
import { CheckoutPage } from '../pageObjects/CheckoutPage';
import { LoginPage } from '../pageObjects/LoginPage';
import { UserAccountAPI } from '../pageObjects/UserAccountAPI';
import { ProductAPI } from '../pageObjects/ProductAPI';
import { generateUniqueEmail } from '../../utils/generateUniqueEmail';
import * as userData from '../../testData/userData.json';
import { getApiUrl } from '../../utils/envHelpers';
import dotenv from 'dotenv';
import path from 'path';

// Load environment variables
dotenv.config({ path: path.resolve(__dirname, '../../.env') });

const apiUrl = getApiUrl();

test.describe('Checkout Process', () => {
  let email: string;
  let userApi: UserAccountAPI;
  let productApi: ProductAPI;
  const { password, productSearchTerms: searchTerms, cardDetails } = userData;

  test.beforeAll(async ({ playwright }) => {
    const apiRequestContext = await playwright.request.newContext();
    userApi = new UserAccountAPI(apiRequestContext, apiUrl);
    productApi = new ProductAPI(apiRequestContext, apiUrl);
    email = generateUniqueEmail();

    const response = await userApi.createUser(email, password, { ...userData, email });
    console.log('Create user response:', await response.json());
  });

  test('should complete the checkout process', async ({ page }) => {
    const checkoutPage = new CheckoutPage(page);
    const loginPage = new LoginPage(page);

    await checkoutPage.navigateToHomePage();
    await loginPage.login(email, password);

    for (const term of searchTerms) {
      await page.goto(`/products?search=${encodeURIComponent(term)}`);
      await productApi.searchProduct(term);
      await checkoutPage.addProductToCart(term);
    }

    await checkoutPage.proceedToCheckout();
    await checkoutPage.placeOrder();
    await checkoutPage.fillCardDetails(cardDetails);
    await checkoutPage.confirmOrder();
    await checkoutPage.expectOrderConfirmation();
  });

  test.afterAll(async () => {
    const response = await userApi.deleteUser(email, password);
    console.log('Delete user response:', await response.json());
  });
});
