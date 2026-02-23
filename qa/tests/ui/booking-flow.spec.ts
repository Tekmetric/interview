import { test, expect } from '../../fixtures/test-fixtures';
import { generateGuestDetails, generateBookingDates } from '../../utils/test-data';

test.describe('Booking Flow - End to End', () => {
  test('should complete booking flow successfully', async ({ homePage, bookingPage, confirmationPage }) => {
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
    

    await homePage.selectFirstRoom(dates.checkin, dates.checkout);
    await bookingPage.clickReserveNowToShowForm();

    const guestDetails = generateGuestDetails();
    await bookingPage.fillGuestDetails(guestDetails);
    await bookingPage.submitBooking();

    await confirmationPage.assertBookingConfirmed();
  });
});