import { Page, Locator, expect } from '@playwright/test';


export class ConfirmationPage {
  readonly page: Page;
  readonly bookingConfirmedHeading: Locator;
  readonly confirmationMessage: Locator;
  readonly returnHomeLink: Locator;

  constructor(page: Page) {
    this.page = page;

 
    this.bookingConfirmedHeading = page.locator('h2.card-title', {
      hasText: 'Booking Confirmed'
    });

    this.confirmationMessage = page.locator('p', {
      hasText: /your booking has been confirmed/i
    });

  
    this.returnHomeLink = page.locator('a', {
      hasText: 'Return home'
    });
  }
  async assertBookingConfirmed() {
    await expect(this.bookingConfirmedHeading).toBeVisible({ timeout: 20000 });
    await expect(this.confirmationMessage).toBeVisible();
    await expect(this.returnHomeLink).toBeVisible();
  }

  async waitForConfirmation(): Promise<void> {
    await this.page.waitForLoadState('networkidle');
    await this.page.waitForTimeout(2000);
  }

  async getConfirmationMessage(): Promise<string> {
    await this.waitForConfirmation();
    if (await this.bookingConfirmedHeading.isVisible()) {
      return (await this.bookingConfirmedHeading.textContent()) || '';
    }
    if (await this.confirmationMessage.isVisible()) {
      return (await this.confirmationMessage.textContent()) || '';
    }
    return '';
  }

  async getBookingId(): Promise<string | null> {
    return null;
  }

  async verifyBookingSuccess(): Promise<boolean> {
    try {
      await this.page.waitForLoadState('networkidle');
      await this.page.waitForTimeout(5000);
      const hasHeading = await this.bookingConfirmedHeading.isVisible();
      const hasMessage = await this.confirmationMessage.isVisible();
      const hasReturnHome = await this.returnHomeLink.isVisible();
      return hasHeading || hasMessage || hasReturnHome;
    } catch {
      return false;
    }
  }
}
