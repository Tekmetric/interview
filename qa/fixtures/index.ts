import { test as base, expect } from '@playwright/test';
import { HomePage } from '../page-objects/home-page';
import { ReservationPage } from '../page-objects/reservation-page';
import { ContactComponent } from '../page-objects/components/contact-component';
import { getApiToken} from '../helpers/auth-helper';

type UIFixtures = {
  homePage: HomePage;
  reservationPage: ReservationPage;
  contactComponent: ContactComponent;
  adminPage: void;
};

type APIFixtures = {
  apiToken: string;
};

type Fixtures = UIFixtures & APIFixtures;

export const test = base.extend<Fixtures>({
  homePage: async ({ page }, use) => {
    const homePage = new HomePage(page);
    await homePage.goto();
    await use(homePage);
  },

  reservationPage: async ({ page }, use) => {
    const reservationPage = new ReservationPage(page);
    await use(reservationPage);
  },

  apiToken: async ({ request }, use) => {
    const token = await getApiToken(request);
    await use(token);
  },

  adminPage: async ({ page }, use) => {
    await page.goto('/admin');
    await use();
  },

  contactComponent: async ({ page }, use) => {
    await page.goto('/');
    await page.locator('#contact').scrollIntoViewIfNeeded();
    await page.locator('#contact').waitFor({ state: 'visible' });
    await use(new ContactComponent(page));
  },
});
// re-export expect so tests only need to import from fixtures
export { expect };
