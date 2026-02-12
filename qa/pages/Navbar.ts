import { Page, Locator, expect } from "@playwright/test";

/**
 * Navbar - persists across all pages
 */
export class Navbar {
  readonly page: Page;

  // Navigation Links
  readonly homeLink: Locator;
  readonly productsLink: Locator;
  readonly cartLink: Locator;
  readonly signupLoginLink: Locator;

  // Account Actions
  readonly logoutLink: Locator;
  readonly deleteAccountLink: Locator;

  constructor(page: Page) {
    this.page = page;

    // Navigation
    this.homeLink = page.getByRole("link", { name: "Home" });
    this.productsLink = page.getByRole("link", { name: "Products" });
    this.cartLink = page.getByRole("link", { name: "Cart" });
    this.signupLoginLink = page.getByRole("link", { name: "Signup / Login" });

    // Account Actions
    this.logoutLink = page.getByRole("link", { name: "Logout" });
    this.deleteAccountLink = page.getByRole("link", { name: "Delete Account" });
  }

  async goToSignupLogin() {
    await this.signupLoginLink.click();
    await this.page.waitForURL("**/login");
  }

  async logout() {
    await this.logoutLink.click();
    await this.page.waitForURL("**/login");
  }

  // Gets the "Logged in as..." username
  get loggedInUsername() {
    return this.page.locator("a:has(.fa-user) b");
  }
}
