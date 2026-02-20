const RESERVATION_SELECTORS = {
  bookingForm: 'form:has(input[placeholder="Firstname"])',
  firstnamePlaceholder: 'Firstname',
  lastnamePlaceholder: 'Lastname',
  emailPlaceholder: 'Email',
  phonePlaceholder: 'Phone',
  reserveButtonName: 'Reserve Now',
  bookingConfirmedText: 'Booking Confirmed',
  bookingValidationErrorText: /size must be between 11 and 21/i
};

class ReservationPage {
  constructor(page) {
    this.page = page;
    this.firstnameInput = this.page.getByPlaceholder(RESERVATION_SELECTORS.firstnamePlaceholder);
    this.lastnameInput = this.page.getByPlaceholder(RESERVATION_SELECTORS.lastnamePlaceholder);
    this.emailInput = this.page.getByPlaceholder(RESERVATION_SELECTORS.emailPlaceholder);
    this.phoneInput = this.page.getByPlaceholder(RESERVATION_SELECTORS.phonePlaceholder);
    this.bookingForm = this.page.locator(RESERVATION_SELECTORS.bookingForm);
    this.openBookingFormButton = this.page.getByRole('button', {
      name: RESERVATION_SELECTORS.reserveButtonName
    }).first();
    this.submitBookingButton = this.bookingForm.getByRole('button', {
      name: RESERVATION_SELECTORS.reserveButtonName
    });
    this.bookingConfirmedBannerText = this.page.getByText(RESERVATION_SELECTORS.bookingConfirmedText);
    this.bookingConfirmedCard = this.page.locator('div').filter({
      has: this.bookingConfirmedBannerText
    }).first();
    this.bookingValidationErrorText = this.page.getByText(RESERVATION_SELECTORS.bookingValidationErrorText);
  }

  async goto(roomId, checkinIso, checkoutIso) {
    await this.page.goto(`/reservation/${roomId}?checkin=${checkinIso}&checkout=${checkoutIso}`);
  }

  async openBookingForm() {
    const isFormAlreadyOpen = await this.firstnameInput.isVisible().catch(() => false);
    if (isFormAlreadyOpen) return;

    await this.openBookingFormButton.click();
    await this.firstnameInput.waitFor({ state: 'visible' });
  }

  async fillGuestForm(data) {
    await this.firstnameInput.fill(data.firstname);
    await this.lastnameInput.fill(data.lastname);
    await this.emailInput.fill(data.email);
    await this.phoneInput.fill(data.phone);
  }

  async submitBooking(options = {}) {
    const { waitForResponse = true, responseTimeout = 10_000 } = options;
    let bookingResponsePromise = null;

    if (waitForResponse) {
      bookingResponsePromise = this.page.waitForResponse(
        (response) => response.request().method() === 'POST' && response.url().includes('/api/booking'),
        { timeout: responseTimeout }
      );
    }

    await this.submitBookingButton.click();
    return bookingResponsePromise ? bookingResponsePromise.catch(() => null) : null;
  }

  async clickReserveInGuestForm() {
    await this.submitBooking({ waitForResponse: false });
  }

  bookingConfirmedBanner() {
    return this.bookingConfirmedBannerText;
  }

  bookingValidationError() {
    return this.bookingValidationErrorText;
  }

  bookingConfirmationDate(dateIso) {
    return this.bookingConfirmedCard.getByText(dateIso);
  }

  async expectBookingConfirmed(expect, bookingWindow) {
    await expect(this.bookingConfirmedBanner()).toBeVisible();
    await expect(this.bookingConfirmationDate(bookingWindow.checkinIso)).toBeVisible();
    await expect(this.bookingConfirmationDate(bookingWindow.checkoutIso)).toBeVisible();
  }
}

module.exports = {
  ReservationPage
};
