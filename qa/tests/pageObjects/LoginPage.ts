import { Page, expect, Locator } from '@playwright/test';

export class LoginPage {
  private page: Page;
  private selectors: Record<string, Locator>;

  constructor(page: Page) {
    this.page = page;
    this.selectors = {
      emailInput: page.locator('[data-qa="login-email"]'),
      passwordInput: page.locator('[data-qa="login-password"]'),
      loginButton: page.locator('[data-qa="login-button"]'),
      loginLink: page.locator('a[href="/login"]'),
      logoutLink: page.locator('a[href="/logout"]'),
      loginHeaderText: page.locator('div[class="login-form"] h2'),
    };
  }

  private userInfoSelector(firstName: string, lastName: string): string {
    return `text=Logged in as ${firstName} ${lastName}`;
  }

  async navigateToLoginPage() {
    await this.page.goto('https://www.automationexercise.com');
    await expect(this.page).toHaveTitle(/Automation Exercise/);
    await this.selectors.loginLink.click();
  }

  async verifyLoginUI() {
  for (const [name, locator] of Object.entries(this.selectors)) {
    // Check if the current element is the logout link, and if so, skip it
    if (name === 'logoutLink') continue;

    // Ensure each of the selectors is visible within the timeout period
    await expect(locator).toBeVisible({ timeout: 5000 });

    // Optionally log the visibility status
    // console.log(`${name} is visible`);
  }

    await expect(this.selectors.loginHeaderText).toHaveText('Login to your account');

    // Check placeholders
    await expect(this.selectors.emailInput).toHaveAttribute('placeholder', 'Email Address');
    await expect(this.selectors.passwordInput).toHaveAttribute('placeholder', 'Password');
  }

  async login(email: string, password: string) {
    await this.navigateToLoginPage();

    const emailInput = this.selectors.emailInput;
    await emailInput.waitFor({ state: 'visible', timeout: 60000 });
    await emailInput.fill(email);
    await this.selectors.passwordInput.fill(password);
    await this.selectors.loginButton.click();
  }

  async verifyLoggedInUser(firstName: string, lastName: string) {
    const loggedInText = this.page.locator(this.userInfoSelector(firstName, lastName));
    await expect(loggedInText).toBeVisible();
    await expect(loggedInText).toHaveText(`Logged in as ${firstName} ${lastName}`);
  }

  async verifyLoginFailure() {
    await expect(this.page).toHaveURL('https://www.automationexercise.com/login');
  }

  async logout() {
    await this.selectors.logoutLink.click();
    await expect(this.page).toHaveURL('https://www.automationexercise.com/login');
    await expect(this.selectors.logoutLink).not.toBeVisible();
  }
}
