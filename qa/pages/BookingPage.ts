import { Page, Locator } from '@playwright/test';
import { GuestDetails, BookingDates } from '../utils/test-data';

export class BookingPage {
  readonly page: Page;
  readonly firstnameInput: Locator;
  readonly lastnameInput: Locator;
  readonly emailInput: Locator;
  readonly phoneInput: Locator;
  readonly cancelButton: Locator;
  readonly bookThisRoomHeading: Locator;
  

  constructor(page: Page) {
    this.page = page;
    this.firstnameInput = page.locator('input[name="firstname"]').or(page.getByLabel(/first/i));
    this.lastnameInput = page.locator('input[name="lastname"]').or(page.getByLabel(/last/i));
    this.emailInput = page.locator('input[name="email"]').or(page.getByLabel(/email/i));
    this.phoneInput = page.locator('input[name="phone"]').or(page.getByLabel(/phone/i));
    this.cancelButton = page.getByRole('button', { name: /cancel/i });
    this.bookThisRoomHeading = page.getByRole('heading', { name: /book this room/i });

  
  }

  async clickReserveNowToShowForm(): Promise<void> {
    const reserveBtn = this.page.locator('button', { hasText: /reserve now/i })
      .or(this.page.getByRole('button', { name: /reserve now/i }));
    await reserveBtn.first().waitFor({ state: 'visible', timeout: 20000 });
    await reserveBtn.first().click({ timeout: 15000 });
    await this.page.waitForTimeout(1500);
  }

  async fillGuestDetails(guest: GuestDetails): Promise<void> {
    await this.firstnameInput.waitFor({ state: 'visible', timeout: 10000 });
    await this.firstnameInput.scrollIntoViewIfNeeded();
    await this.firstnameInput.fill(guest.firstname);
    await this.lastnameInput.fill(guest.lastname);
    await this.emailInput.fill(guest.email);
    await this.phoneInput.fill(guest.phone);
  }

  async fillBookingForm(dates: BookingDates, guest: GuestDetails): Promise<void> {
    await this.fillGuestDetails(guest);
  }

  async submitBooking(): Promise<number> {
  const reserveBtn = this.page
    .getByRole('button', { name: /reserve now/i })
    .or(this.page.locator('button').filter({ hasText: /reserve now/i }));

  await reserveBtn.last().scrollIntoViewIfNeeded();

  const responsePromise = this.page.waitForResponse(
    (r) =>
      r.url().includes('/api/booking') &&
      r.request().method() === 'POST',
    { timeout: 15000 }
  );

  await reserveBtn.last().click();

  const response = await responsePromise;

  if (!response.ok()) {
    throw new Error(`Booking creation failed: ${response.status()}`);
  }

  const responseBody = await response.json();

  const bookingId = responseBody.bookingid;

  if (!bookingId) {
    throw new Error('Booking ID not found in response');
  }

  return bookingId;
}

  async isFormVisible(): Promise<boolean> {
    try {
      await this.firstnameInput.waitFor({ state: 'visible', timeout: 5000 });
      return true;
    } catch {
      return false;
    }
  }
  async assertBookingPageOpened(): Promise<void> {
  await this.bookThisRoomHeading.waitFor({ state: 'visible', timeout: 10000 });
  }
  
}
