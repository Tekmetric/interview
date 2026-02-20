const { test, expect } = require('../../fixtures/test.fixture');
const { createBookingWithRetry } = require('../../helpers/booking-flow-helper');

test.describe('UI Booking Flow', () => {
  test('guest can choose room, choose dates, submit booking, and see confirmation', async ({
    homePage,
    reservationPage,
    automationApi,
    availableRoomWindow,
    guest
  }) => {
    const { bookingWindow, bookingResponse } = await createBookingWithRetry({
      homePage,
      reservationPage,
      automationApi,
      initialWindow: availableRoomWindow,
      guest
    });

    expect(bookingResponse.status()).toBe(201);
    await reservationPage.expectBookingConfirmed(expect, bookingWindow);
  });
});
