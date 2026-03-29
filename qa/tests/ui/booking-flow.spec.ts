import { test, expect } from '../../fixtures/test-fixtures';
import { generateGuestDetails, generateBookingDates } from '../../utils/test-data';

test.describe('Booking Flow - End to End', () => {

  let createdBookingId: number | undefined;

  test.afterEach(async ({ apiClient }) => {
    if (createdBookingId) {
      const token = await apiClient.authenticateForBookingApi();
      await apiClient.deleteBooking(createdBookingId, token);
    }
  });

  test('should complete booking flow successfully', async ({
    homePage,
    bookingPage,
    confirmationPage,
    apiClient
  }) => {

    test.setTimeout(60000);

    await homePage.goto();

    const dates = generateBookingDates();
    await homePage.fillDates(dates.checkin, dates.checkout);
    await homePage.clickCheckAvailability();

    const roomCount = await homePage.getRoomCount();
    expect(roomCount).toBeGreaterThan(0);

    const pricePerNight = await homePage.getFirstRoomPrice();

    await homePage.selectFirstRoom(dates.checkin, dates.checkout);

    await bookingPage.assertBookingPageOpened();

    await homePage.assertTotalCalculation(
      pricePerNight,
      dates.checkin,
      dates.checkout
    );

    await bookingPage.clickReserveNowToShowForm();

    const guestDetails = generateGuestDetails();
    await bookingPage.fillGuestDetails(guestDetails);

    // 🔥 Capture booking ID
    createdBookingId = await bookingPage.submitBooking();

    await confirmationPage.assertBookingConfirmed();
  });
});
