import { test, expect } from '@playwright/test';

test('New user can create an account with valid data', async ({ page }) => {
    await page.goto('https://www.automationexercise.com/login');

    await expect(page.getByText('New User Signup!')).toBeVisible();

    const uniqueEmail = `testuser_${Date.now()}@example.com`;

    await page.locator('[data-qa="signup-name"]').fill('Test User');
    await page.locator('[data-qa="signup-email"]').fill(uniqueEmail);
    await page.locator('[data-qa="signup-button"]').click();

    await expect(page.getByText('Enter Account Information')).toBeVisible();

    await page.locator('#id_gender1').check();
    await page.locator('#password').fill('StrongPassword987!');

    await page.locator('#days').selectOption('1');
    await page.locator('#months').selectOption('1');
    await page.locator('#years').selectOption('1995');

    await page.getByLabel('Sign up for our newsletter!').check();
    await page.getByLabel('Receive special offers from our partners!').check();

    await page.locator('#first_name').fill('FirstUser');
    await page.locator('#last_name').fill('LastUser');
    await page.locator('#company').fill('testerCompany');
    await page.locator('#address1').fill('111 testBuilding');

    await page.locator('#country').selectOption({ label: 'United States' });

    await page.locator('#state').fill('WI');
    await page.locator('#city').fill('Middleton');
    await page.locator('#zipcode').fill('222333');
    await page.locator('#mobile_number').fill('1112223344');

    await page.locator('[data-qa="create-account"]').click();

    await expect(page.getByText('Account Created!')).toBeVisible();

});
