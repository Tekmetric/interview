const HOME_SELECTORS = {
  availabilityCard: 'div.card:has(button:has-text("Check Availability"))',
  stayDateInputs: 'input',
  checkAvailabilityButtonName: 'Check Availability',
  roomReservationLinkById: (roomId) => `a[href*="/reservation/${roomId}?"]`
};

class HomePage {
  constructor(page) {
    this.page = page;
    this.availabilityCard = this.page.locator(HOME_SELECTORS.availabilityCard);
    this.checkinInput = this.availabilityCard.locator(HOME_SELECTORS.stayDateInputs).first();
    this.checkoutInput = this.availabilityCard.locator(HOME_SELECTORS.stayDateInputs).nth(1);
    this.checkAvailabilityButton = this.availabilityCard.getByRole('button', {
      name: HOME_SELECTORS.checkAvailabilityButtonName
    });
  }

  async goto() {
    await this.page.goto('/');
  }

  async fillStayDates(checkinUi, checkoutUi) {
    await this.checkinInput.fill(checkinUi);
    await this.checkoutInput.fill(checkoutUi);
  }

  async checkAvailability() {
    await this.checkAvailabilityButton.click();
  }

  async openRoomReservation(roomId) {
    await this.page.locator(HOME_SELECTORS.roomReservationLinkById(roomId)).first().click();
  }
}

module.exports = {
  HomePage
};
