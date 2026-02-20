async function createBookingWithRetry({
  homePage,
  reservationPage,
  automationApi,
  initialWindow,
  guest,
  maxAttempts = 3,
  windowSearchOptions = { nights: 2, startOffsetDays: 30, searchDays: 180 }
}) {
  for (let attempt = 1; attempt <= maxAttempts; attempt += 1) {
    const bookingWindow =
      attempt === 1 ? initialWindow : await automationApi.findAvailableRoomWindow(windowSearchOptions);

    await homePage.goto();
    await homePage.fillStayDates(bookingWindow.checkinUi, bookingWindow.checkoutUi);
    await homePage.checkAvailability();
    await homePage.openRoomReservation(bookingWindow.roomId);

    await reservationPage.openBookingForm();
    await reservationPage.fillGuestForm(guest);

    const bookingResponse = await reservationPage.submitBooking();
    const status = bookingResponse ? bookingResponse.status() : null;

    if (status === 201) {
      return { bookingWindow, bookingResponse };
    }

    if (status !== 409) {
      throw new Error(`Booking failed with unexpected status: ${status}`);
    }
  }

  throw new Error('Booking was not created after retries because room windows became unavailable');
}

module.exports = {
  createBookingWithRetry
};
