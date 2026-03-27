import { test, expect } from "@playwright/test";
import { Navbar } from "../../../pages/Navbar";
import { LoginSignupPage } from "../../../pages/LoginSignupPage";
import { SignupFormPage } from "../../../pages/SignupFormPage";
import { generateUserData, UserData } from "../../../utils/test-data-generator";
import { AccountApiClient } from "../../../api/AccountApiClient";

test.describe("e2e - happy and unhappy sign up", () => {
  let navbar: Navbar;
  let loginSignupPage: LoginSignupPage;
  let signupFormPage: SignupFormPage;
  let accountApi: AccountApiClient;
  let user: UserData;

  test.beforeEach(async ({ page, request }) => {
    navbar = new Navbar(page);
    loginSignupPage = new LoginSignupPage(page);
    signupFormPage = new SignupFormPage(page);
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

  test("should create new user account when all required fields are valid @signup @happy", async ({
    page,
  }) => {
    // Assert signup heading is visible
    await expect(loginSignupPage.signupHeading).toBeVisible();

    // Fill in name and email on signup form and click 'Signup' button
    await loginSignupPage.fillAndSubmitNameAndEmail(user.fullName, user.email);

    // Assert 'Name' field is pre-filled with the name entered on previous signup page
    await expect(signupFormPage.nameInput).toHaveValue(user.fullName);

    // Assert 'Name' field is editable (enabled)
    await expect(signupFormPage.nameInput).toBeEnabled();

    // Assert 'Email' field is pre-filled
    await expect(signupFormPage.emailInput).toHaveValue(user.email);

    // Assert 'Email' field in not editable (diabled)
    await expect(signupFormPage.emailInput).toBeDisabled();

    // Fill all required fields on signup form and submit
    await signupFormPage.fillAndSubmitSignupForm(user);

    // Assert redirect to /account_created
    await expect(page).toHaveURL("/account_created");

    // Assert 'Account Created!' header
    await expect(
      page.getByRole("heading", { name: "Account Created!" })
    ).toBeVisible();

    // Assert 'Congratulations!...' text
    await expect(
      page.getByText(
        "Congratulations! Your new account has been successfully created!"
      )
    ).toBeVisible();

    // Click 'Continue' link (looks like button)
    await page.getByRole("link", { name: "Continue" }).click();

    // Assert redirect to homepage
    await expect(page).toHaveURL("/");

    // Assert 'Logout' link is visible
    await expect(navbar.logoutLink).toBeVisible();

    // Assert 'Delete Account' link is visible
    await expect(navbar.deleteAccountLink).toBeVisible();

    // Assert 'Logged in as {name}' text is the user's name
    await expect(navbar.loggedInUsername).toHaveText(user.fullName);
  });

  test("should show error when signing up with existing email @signup @unhappy", async ({
    page,
  }) => {
    // Create account via API
    await accountApi.createAccountFromUserData(user);

    // Call verify login API with valid user credentials
    const response = await accountApi.verifyLoginApi(user.email, user.password);

    // Assert 200 response
    expect(response.responseCode).toBe(200);

    // Fill signup name field
    await loginSignupPage.fillSignupName(user.firstName);

    // Fill signup email field (email already exists/take)
    await loginSignupPage.fillSignupEmail(user.email);

    // Click 'Login' button
    await loginSignupPage.signupButton.click();

    // Assert login error message is visible
    await expect(loginSignupPage.emailExistsErrorMessage).toBeVisible();

    // Assert page URL is still /login (fails here due to URL changing to /signup - unsure if intended)
    await expect(page).toHaveURL("/login");

    // Assert signup header is still visible
    await expect(loginSignupPage.signupHeading).toBeVisible();
  });
});
