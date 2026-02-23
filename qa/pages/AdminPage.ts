/**
 * Page Object Model for Admin Page
 */

import { Page, Locator } from '@playwright/test';
import { BASE_URL } from '../utils/constants';

export class AdminPage {
  readonly page: Page;
  readonly usernameInput: Locator;
  readonly passwordInput: Locator;
  readonly loginButton: Locator;
  readonly adminDashboard: Locator;
  readonly bookingsList: Locator;
  readonly logoutButton: Locator;

  constructor(page: Page) {
    this.page = page;
    
    // Login form elements
    this.usernameInput = page.getByLabel(/username/i)
      .or(page.locator('input[name="username"]'))
      .or(page.locator('#username'))
      .or(page.locator('input[type="text"]').first());
    
    this.passwordInput = page.getByLabel(/password/i)
      .or(page.locator('input[name="password"]'))
      .or(page.locator('#password'))
      .or(page.locator('input[type="password"]'));
    
    this.loginButton = page.getByRole('button', { name: /login/i })
      .or(page.locator('button[type="submit"]'))
      .or(page.locator('#loginButton'));

    // Admin dashboard elements (after login)
    this.adminDashboard = page.locator('[data-testid="admin-dashboard"]')
      .or(page.locator('.admin-dashboard'))
      .or(page.getByText(/dashboard|admin/i));
    
    this.bookingsList = page.locator('[data-testid="bookings"]')
      .or(page.locator('.bookings-list'))
      .or(page.locator('table'))
      .or(page.getByText(/bookings/i));
    
    this.logoutButton = page.getByRole('button', { name: /logout/i })
      .or(page.getByRole('link', { name: /logout/i }));
  }

  /**
   * Navigate to admin page
   */
  async goto(): Promise<void> {
    await this.page.goto(`${BASE_URL}/admin`);
    await this.page.waitForLoadState('networkidle');
  }

  /**
   * Check if login form is visible
   */
  async isLoginFormVisible(): Promise<boolean> {
    try {
      await this.usernameInput.waitFor({ state: 'visible', timeout: 5000 });
      return true;
    } catch {
      return false;
    }
  }

  async isDashboardVisible(): Promise<boolean> {
    await this.page.waitForLoadState('domcontentloaded');
    await this.page.waitForTimeout(2000);
    const loginVisible = await this.isLoginFormVisible();
    if (loginVisible) return false;
    const hasAdminContent = await this.page.getByRole('link', { name: /room|booking|branding|report|message/i }).first().isVisible().catch(() => false)
      || await this.page.getByText(/room|booking|branding|report|message/i).first().isVisible().catch(() => false);
    return hasAdminContent;
  }

  /**
   * Login via UI (not recommended - use API auth instead)
   */
  async login(username: string, password: string): Promise<void> {
    await this.usernameInput.fill(username);
    await this.passwordInput.fill(password);
    await this.loginButton.click();
    await this.page.waitForLoadState('networkidle');
  }

  /**
   * Verify admin access (should work without UI login if session is set)
   */
  async verifyAdminAccess(): Promise<boolean> {
    await this.goto();
    return await this.isDashboardVisible();
  }
}
