import { Page, Locator } from "@playwright/test";

export class ReservationPage {
  readonly firstNameInput: Locator;
  readonly lastNameInput: Locator;
  readonly emailInput: Locator;
  readonly phoneInput: Locator;

  readonly doReserveNowButton: Locator;

  readonly guestDetailsForm: Locator;
  readonly priceSummary: Locator;

  readonly reservationCalendar: Locator;
  readonly reservationCalendarNextButton: Locator;
  readonly reservationMonthLabel: Locator;
  readonly reserveNowSubmitButton: Locator;
  readonly selectedDateRange: Locator;

  readonly confirmationHeading: Locator;
  readonly confirmedDates: Locator;
  readonly returnHomeButton: Locator;

  constructor(private page: Page) {
    this.firstNameInput = page.getByLabel("Firstname");
    this.lastNameInput = page.getByLabel("Lastname");
    this.emailInput = page.getByLabel("Email");
    this.phoneInput = page.getByLabel("Phone");

    this.doReserveNowButton = page.locator("#doReservation");

    this.guestDetailsForm = page.locator(".room-booking-form");
    this.priceSummary = page.locator(".booking-card").getByText("nights");

    this.reservationCalendar = page.locator(".rbc-calendar");
    this.reservationCalendarNextButton = this.reservationCalendar.getByRole(
      "button",
      { name: "Next" },
    );
    this.reservationMonthLabel =
      this.reservationCalendar.locator(".rbc-toolbar-label");
    this.selectedDateRange = this.reservationCalendar
      .locator('.rbc-event-content[title="Selected"]')
      .first();

    this.reserveNowSubmitButton = page.getByRole("button", {
      name: "Reserve Now",
    });

    this.confirmationHeading = page.getByRole("heading", {
      name: "Booking Confirmed",
    });
    this.confirmedDates = page.locator(".booking-card").locator("strong");
    this.returnHomeButton = page.getByRole("link", { name: "Return home" });
  }

  private getDateCell(day: number): Locator {
    return this.reservationCalendar
      .locator(".rbc-date-cell:not(.rbc-off-range)")
      .filter({ hasText: new RegExp(`^${day}$`) });
  }

  async selectDateRange(checkInDay: number, checkOutDay: number) {
    const checkInCell = this.getDateCell(checkInDay);
    const checkOutCell = this.getDateCell(checkOutDay);

    const checkInBox = await checkInCell.boundingBox();
    const checkOutBox = await checkOutCell.boundingBox();

    if (!checkInBox || !checkOutBox) {
      throw new Error("Could not find date cells for selection");
    }

    await this.page.mouse.move(
      checkInBox.x + checkInBox.width / 2,
      checkInBox.y + checkInBox.height / 2,
    );
    await this.page.mouse.down();

    await this.page.mouse.move(
      checkOutBox.x + checkOutBox.width / 2,
      checkOutBox.y + checkOutBox.height / 2,
      { steps: 10 },
    );
    await this.page.mouse.up();
  }

  async navigatingToMonth(targetDate: {
    year: number;
    month: number;
    day: number;
  }) {
    const monthName = new Date(
      targetDate.year,
      targetDate.month - 1,
    ).toLocaleString("default", { month: "long" });
    const targetDateLabel = `${monthName} ${targetDate.year}`;
    let attempts = 0;

    while (
      (await this.reservationMonthLabel.innerText()) !== targetDateLabel &&
      attempts < 24
    ) {
      await this.reservationCalendarNextButton.click();
      await this.reservationMonthLabel.waitFor({ state: "visible" });
      attempts++;
    }
  }

  async fillOutGuestDetails({
    firstName,
    lastName,
    email,
    phone,
  }: {
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
  }) {
    await this.firstNameInput.fill(firstName);
    await this.lastNameInput.fill(lastName);
    await this.emailInput.fill(email);
    await this.phoneInput.fill(phone);
  }

  async openGuestDetailsForm() {
    await this.doReserveNowButton.click();
    await this.guestDetailsForm.waitFor({ state: "visible" });
  }

  async submitBooking() {
    await this.reserveNowSubmitButton.click();
  }
}
