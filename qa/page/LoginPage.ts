import { Page } from '@playwright/test';

export class LoginPage {
  private page: Page;

  // Selectors
  private nameInput = 'input[placeholder="Name"]';
  private emailInput = 'input[data-qa="signup-email"]';
  private signupButton = 'button:has-text("Signup")';

  private loginEmailInput = '//input[@data-qa="login-email"]';
  private loginPasswordInput = '//input[@data-qa="login-password"]';
  private loginButton = '//button[@data-qa="login-button"]';


  constructor(page: Page) {
    this.page = page;
  }

  // Navigate to the Signup Page
  async navigate() {
    await this.page.goto('https://automationexercise.com/login');
  }

  // Perform Signup
  async signup(name: string, email: string) {
    await this.page.fill(this.nameInput, name);
    await this.page.fill(this.emailInput, email);
    await this.page.click(this.signupButton);
    await this.page.waitForTimeout(2000);
  }

  // Verify successful signup navigation
  async verifySignupPage() {
    await this.page.waitForURL(/signup/, { timeout: 5000 });
  }

  // Perform Login
  async login(email: string, password: string) {
    await this.page.fill(this.loginEmailInput, email); 
    await this.page.fill(this.loginPasswordInput, password); 
    await this.page.click(this.loginButton);
  }
}



