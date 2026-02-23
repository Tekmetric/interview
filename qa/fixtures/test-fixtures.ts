import { test as base } from '@playwright/test';
import { ApiClient } from '../api/api-client';
import { SessionManager } from '../helpers/session-manager';
import { HomePage } from '../pages/HomePage';
import { BookingPage } from '../pages/BookingPage';
import { ConfirmationPage } from '../pages/BookingConfirmationPage';
import { AdminPage } from '../pages/AdminPage';

type TestFixtures = {
  apiClient: ApiClient;
  authenticatedContext: any;
  homePage: HomePage;
  bookingPage: BookingPage;
  confirmationPage: ConfirmationPage;
  adminPage: AdminPage;
};

export const test = base.extend<TestFixtures>({
  apiClient: async ({ request }, use) => {
    const apiClient = new ApiClient(request);
    await use(apiClient);
  },
  authenticatedContext: async ({ browser, apiClient }, use) => {
    const context = await browser.newContext();
    await SessionManager.setupAuthenticatedContext(context, apiClient);
    
    await use(context);
    await context.close();
  },
  homePage: async ({ page }, use) => {
    const homePage = new HomePage(page);
    await use(homePage);
  },
  bookingPage: async ({ page }, use) => {
    const bookingPage = new BookingPage(page);
    await use(bookingPage);
  },
  confirmationPage: async ({ page }, use) => {
    const confirmationPage = new ConfirmationPage(page);
    await use(confirmationPage);
  },
  adminPage: async ({ page }, use) => {
    const adminPage = new AdminPage(page);
    await use(adminPage);
  },
});

export { expect } from '@playwright/test';
