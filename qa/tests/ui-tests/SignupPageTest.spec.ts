import { test, expect } from '@playwright/test';
import { SignupPage } from '../../page/SignupPage';
import { LoginPage } from '../../page/LoginPage';
import { getRandomUser } from '../../utils/UserAccount';

test('Fill and submit the account creation form', async ({ page }) => {
    const signupPage = new SignupPage(page);
    const loginPage = new LoginPage(page);
    let userEmail = `testuser${Date.now()}@example.com`;

    await loginPage.navigate();

    const user = await getRandomUser();
    await loginPage.signup("Alan Test", userEmail);
    // await loginPage.signup(user.name, user.email);
    // await page.waitForURL(/signup/, { timeout: 2000 });

    await signupPage.selectTitle("Mr");
    await signupPage.enterPassword("SecurePassword123");
    await signupPage.selectDateOfBirth("1", "January", "1990");
    await signupPage.toggleNewsletterSubscription(true);
    await signupPage.toggleSpecialOffersSubscription(false);
    await page.waitForTimeout(2000)

    await signupPage.fillAddressDetails(
        user.name.substring(0, user.name.indexOf(' ')),
        user.name.substring(user.name.indexOf(' ')),
        user.company.name,
        user.address.street,
        'United States',
        'WA',
        user.address.city,
        user.address.zipcode,
        user.phone
    );

    await signupPage.submitForm();

    // Assert successful form submission
    await signupPage.verifyAccountCreation();
    await signupPage.submitContinue()
});
