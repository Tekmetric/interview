import { test, expect } from '../../fixtures/test-fixtures';
import { generateGuestDetails, generateBookingDates } from '../../utils/test-data';
import { BookingRequest } from '../../api/api-client';

test.describe('Booking API', () => {
  let authToken: string;

  test.beforeAll(async ({ apiClient }) => {
    authToken = await apiClient.authenticateForBookingApi();
    expect(authToken).toBeTruthy();
  });

  test('should create a new booking', async ({ apiClient }) => {
    const dates = generateBookingDates();
    const guest = generateGuestDetails();
    const bookingRequest: BookingRequest = {
      firstname: guest.firstname,
      lastname: guest.lastname,
      totalprice: 200,
      depositpaid: true,
      bookingdates: { checkin: dates.checkin, checkout: dates.checkout },
      additionalneeds: 'Breakfast',
    };
    const response = await apiClient.createBooking(bookingRequest);
    expect(response.bookingid).toBeDefined();
    expect(response.bookingid).toBeGreaterThan(0);
  });

  test('should get booking by ID', async ({ apiClient }) => {
    const dates = generateBookingDates();
    const guest = generateGuestDetails();
    const bookingRequest: BookingRequest = {
      firstname: guest.firstname,
      lastname: guest.lastname,
      totalprice: 150,
      depositpaid: false,
      bookingdates: { checkin: dates.checkin, checkout: dates.checkout },
    };
    const createResponse = await apiClient.createBooking(bookingRequest);
    const retrievedBooking = await apiClient.getBooking(createResponse.bookingid);
    expect(retrievedBooking.firstname).toBe(bookingRequest.firstname);
    expect(retrievedBooking.lastname).toBe(bookingRequest.lastname);
  });

  test('should update an existing booking', async ({ apiClient }) => {
    const dates = generateBookingDates();
    const guest = generateGuestDetails();
    const originalBooking: BookingRequest = {
      firstname: guest.firstname,
      lastname: guest.lastname,
      totalprice: 100,
      depositpaid: false,
      bookingdates: { checkin: dates.checkin, checkout: dates.checkout },
    };
    const createResponse = await apiClient.createBooking(originalBooking);
    const updatedGuest = generateGuestDetails();
    const updateData: BookingRequest = {
      ...originalBooking,
      firstname: updatedGuest.firstname,
      lastname: updatedGuest.lastname,
      totalprice: 250,
      depositpaid: true,
      additionalneeds: 'Late checkout',
    };
    const updatedBooking = await apiClient.updateBooking(createResponse.bookingid, updateData, authToken);
    expect(updatedBooking.firstname).toBe(updateData.firstname);
    expect(updatedBooking.lastname).toBe(updateData.lastname);
  });

  test('should delete a booking', async ({ apiClient }) => {
    const dates = generateBookingDates();
    const guest = generateGuestDetails();
    const bookingRequest: BookingRequest = {
      firstname: guest.firstname,
      lastname: guest.lastname,
      totalprice: 175,
      depositpaid: true,
      bookingdates: { checkin: dates.checkin, checkout: dates.checkout },
    };
    const createResponse = await apiClient.createBooking(bookingRequest);
    await apiClient.deleteBooking(createResponse.bookingid, authToken);
    try {
      await apiClient.getBooking(createResponse.bookingid);
      expect(true).toBe(false);
    } catch {
      expect(true).toBe(true);
    }
  });

  test('should handle invalid booking ID', async ({ apiClient }) => {
    try {
      await apiClient.getBooking(999999);
      expect(true).toBe(false);
    } catch {
      expect(true).toBe(true);
    }
  });

  test('should get all booking IDs', async ({ apiClient }) => {
    const bookingIds = await apiClient.getAllBookingIds();
    expect(Array.isArray(bookingIds)).toBe(true);
    expect(bookingIds.length).toBeGreaterThan(0);
  });
});
