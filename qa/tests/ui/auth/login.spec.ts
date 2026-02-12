import { test, expect } from "@playwright/test";
import { Navbar } from "../../../pages/Navbar";
import { LoginSignupPage } from "../../../pages/LoginSignupPage";
import {
  generateUserData,
  InvalidTestData,
  UserData,
} from "../../../utils/test-data-generator";
import { AccountApiClient } from "../../../api/AccountApiClient";

test.describe("e2e - happy and unhappy log in", () => {
  let navbar: Navbar;
  let loginSignupPage: LoginSignupPage;
  let accountApi: AccountApiClient;
  let user: UserData;

  test.beforeEach(async ({ page, request }) => {
    navbar = new Navbar(page);
    loginSignupPage = new LoginSignupPage(page);
    accountApi = new AccountApiClient(request);

    // Create user data
    const newUser = generateUserData();
    user = newUser;

    // Navigate to homepage
    await page.goto("/");

    // Click 'Signup / Login' link
    await navbar.signupLoginLink.click();
  });

  test.afterEach(async ({ page }) => {
    // Delete the account via API
    await accountApi.safeDeleteAccount(user.email, user.password);
  });

  test("should log in successfully with valid email and password @login @happy", async ({
    page,
  }) => {
    // Create account via API
    await accountApi.createAccountFromUserData(user);

    // Assert login heading is visible
    await expect(loginSignupPage.loginHeading).toBeVisible();

    // Log in with valid email and password
    await loginSignupPage.login(user.email, user.password);

    // Assert redirect to homepage
    await expect(page).toHaveURL("/");

    // Assert 'Logout' link is visible
    await expect(navbar.logoutLink).toBeVisible();

    // Assert 'Delete Account' link is visible
    await expect(navbar.deleteAccountLink).toBeVisible();

    // Assert 'Logged in as {name}' text is the user's name
    await expect(navbar.loggedInUsername).toHaveText(user.fullName);
  });
  test("should show error when logging in with invalid password @login @unhappy", async ({
    page,
  }) => {
    // Create account via API
    await accountApi.createAccountFromUserData(user);

    // Call verify login API with valid user credentials
    const response = await accountApi.verifyLoginApi(user.email, user.password);

    // Assert 200 response
    expect(response.responseCode).toBe(200);

    // Fill login email field
    await loginSignupPage.fillLoginEmail(user.email);

    // Fill login password field with an incorrect password
    await loginSignupPage.fillLoginPassword("wrongpassword");

    // Click 'Login' button
    await loginSignupPage.loginButton.click();

    // Assert login error message is visible
    await expect(loginSignupPage.invalidCredentialsErrorMessage).toBeVisible();

    // Assert page URL is still /login
    await expect(page).toHaveURL("/login");

    // Assert login header is still visible
    await expect(loginSignupPage.loginHeading).toBeVisible();
  });

  test("should show error when signing up with invalid email (missing domain) @login @unhappy", async ({
    page,
  }) => {
    // Invalid email
    const invalidEmail = InvalidTestData.emails.missingDomain;

    // Wait for login email input to be visible
    await loginSignupPage.loginEmailInput.waitFor({ state: "visible" });

    // Fill login email field
    await loginSignupPage.fillLoginEmail(invalidEmail);

    // Fill login password field with an incorrect password
    await loginSignupPage.fillLoginPassword("wrongpassword");

    // Click 'Login' button
    await loginSignupPage.loginButton.click();

    // Get validation message
    const validationMessage = await loginSignupPage.loginEmailInput.evaluate(
      (element: HTMLInputElement) => element.validationMessage
    );

    // Check if message contains any of these (browser specific form validation messages)
    const possibleMessages = [
      `Please enter a part following '@'. '${invalidEmail}' is incomplete.`, // Chromium
      "Please enter an email address.", // Firefox
      "Enter an email address", // WebKit
    ];

    // Store the result of whether one of the messages was displayed (true/false)
    const hasValidMessage = possibleMessages.some((msg) =>
      validationMessage.includes(msg)
    );

    // Assert that at least one of the validation messages was displayed
    expect(hasValidMessage).toBeTruthy();

    // Assert page URL is still /login
    await expect(page).toHaveURL("/login");

    // Assert login header is still visible
    await expect(loginSignupPage.loginHeading).toBeVisible();
  });
});
