import { Page, Locator } from '@playwright/test';
import { BASE_URL } from '../utils/constants';

export class HomePage {
  readonly page: Page;
  readonly checkInInput: Locator;
  readonly checkOutInput: Locator;
  readonly checkAvailabilityButton: Locator;
  readonly firstBookNowLink: Locator;
  readonly contactNameInput: Locator;
  readonly contactEmailInput: Locator;
  readonly contactPhoneInput: Locator;
  readonly contactSubjectInput: Locator;
  readonly contactMessageInput: Locator;
  readonly contactSubmitButton: Locator;
  readonly contactForm: Locator;
  readonly roomCards: Locator;
  readonly displayedPricePerNight: Locator;
  readonly totalAmount: Locator;

  constructor(page: Page) {
    this.page = page;
    this.checkInInput = page.locator('.react-datepicker-wrapper').first().locator('input')
      .or(page.getByPlaceholder(/check.*in/i))
      .or(page.getByLabel(/check.*in/i));
    this.checkOutInput = page.locator('.react-datepicker-wrapper').nth(1).locator('input')
      .or(page.getByPlaceholder(/check.*out/i))
      .or(page.getByLabel(/check.*out/i));
    this.checkAvailabilityButton = page.getByRole('button', { name: /check availability/i });
    this.firstBookNowLink = page.getByRole('link', { name: /book now/i }).first();
    this.contactForm = page.locator('form').filter({ hasText: /send us a message|message/i });
    this.contactNameInput = page.locator('#name').or(page.getByLabel(/name/i)).first();
    this.contactEmailInput = page.locator('#email').or(page.getByLabel(/email/i)).first();
    this.contactPhoneInput = page.locator('#phone').or(page.getByLabel(/phone/i)).first();
    this.contactSubjectInput = page.locator('#subject').or(page.getByLabel(/subject/i)).first();
    this.contactMessageInput = page.locator('#description').or(page.locator('#message')).or(page.getByLabel(/message/i)).first();
    this.contactSubmitButton = page.getByRole('button', { name: /submit/i });
    this.roomCards = page.locator('.room-card');
      this.displayedPricePerNight = page.locator('.fw-bold.fs-5').first();

    this.totalAmount = page
  .locator('div.d-flex.justify-content-between.fw-bold')
  .locator('span')
  .nth(1);
    
  }

  async goto(): Promise<void> {
    await this.page.goto(BASE_URL);
    await this.page.waitForLoadState('networkidle');
  }

  async fillCheckInDate(dateStr: string): Promise<void> {
    await this.checkInInput.waitFor({ state: 'visible' });
    await this.checkInInput.clear();
    await this.checkInInput.fill(dateStr);
    await this.page.keyboard.press('Tab');
  }

  async fillCheckOutDate(dateStr: string): Promise<void> {
    await this.checkOutInput.waitFor({ state: 'visible' });
    await this.checkOutInput.clear();
    await this.checkOutInput.fill(dateStr);
    await this.page.keyboard.press('Tab');
  }

  async fillDates(checkin: string, checkout: string): Promise<void> {
    await this.fillCheckInDate(checkin);
    await this.fillCheckOutDate(checkout);
  }

  async clickCheckAvailability(): Promise<void> {
    await this.checkAvailabilityButton.click();
    await this.page.waitForLoadState('networkidle');
  }

  async selectFirstRoom(checkin?: string, checkout?: string): Promise<void> {
    let targetUrl = `${BASE_URL}/reservation/1`;
    const bookLinks = this.page.getByRole('link', { name: /book now/i });
    if ((await bookLinks.count()) > 0) {
      const href = await bookLinks.first().getAttribute('href');
      const match = href?.match(/\/reservation\/(\d+)/);
      const roomId = match ? match[1] : '1';
      targetUrl = href?.startsWith('http') ? href : `${BASE_URL}${href?.startsWith('/') ? href : '/reservation/' + roomId}`;
    }
    const url = new URL(targetUrl);
    if (checkin && checkout) {
      url.searchParams.set('checkin', checkin);
      url.searchParams.set('checkout', checkout);
    }
    await this.page.goto(url.toString());
    await this.page.waitForLoadState('networkidle');
    await this.page.waitForTimeout(3000);
  }

  async getRoomCount(): Promise<number> {
    await this.page.waitForTimeout(1000);
    return await this.page.getByRole('link', { name: /book now/i }).count();
  }

  async fillContactForm(data: { name: string; email: string; phone: string; subject: string; message: string }): Promise<void> {
    await this.contactNameInput.fill(data.name);
    await this.contactEmailInput.fill(data.email);
    await this.contactPhoneInput.fill(data.phone);
    await this.contactSubjectInput.fill(data.subject);
    await this.contactMessageInput.fill(data.message);
  }

  async submitContactForm(): Promise<void> {
    await this.contactSubmitButton.click();
    await this.page.waitForLoadState('networkidle');
  }
  async getFirstRoomPrice(): Promise<number> {
  const firstCard = this.roomCards.first();
  const priceText = await firstCard.locator('.fw-bold').innerText();
  return parseInt(priceText.replace('£', '').trim(), 10);
}
async getDisplayedPricePerNight(): Promise<number> {
  const priceText = await this.displayedPricePerNight.innerText();
  return parseInt(priceText.replace('£', '').trim(), 10);
}
async getDisplayedTotal(): Promise<number> {
  const totalText = await this.totalAmount.innerText();
  return parseInt(totalText.replace('£', '').trim(), 10);
}
async assertTotalCalculation(
  expectedRoomPrice: number,
  checkin: string,
  checkout: string
): Promise<void> {

  const checkinDate = new Date(checkin);
  const checkoutDate = new Date(checkout);

  const nights = Math.ceil(
    (checkoutDate.getTime() - checkinDate.getTime()) /
    (1000 * 60 * 60 * 24)
  );

  const cleaningFee = 25;
  const serviceFee = 15;

  const expectedTotal = (expectedRoomPrice * nights) + cleaningFee + serviceFee;

  const displayedTotal = await this.getDisplayedTotal();

  if (displayedTotal !== expectedTotal) {
    throw new Error(
      `Total mismatch: Expected ${expectedTotal} but found ${displayedTotal}`
    );
  }
}
}
