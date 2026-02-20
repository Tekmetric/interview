const { test, expect } = require('../../fixtures/test.fixture');
const { buildBookingPayload } = require('../../data/booking.factory');

test.describe('API Booking - Create and Get', () => {
  test('creates booking and fetches it by id', async ({ restfulBookerApi }, testInfo) => {
    const payload = buildBookingPayload(`C${testInfo.parallelIndex}`);

    const created = await restfulBookerApi.createBooking(payload);
    expect(created.response.status()).toBe(200);
    expect(created.body.bookingid).toBeGreaterThan(0);

    const fetched = await restfulBookerApi.getBooking(created.body.bookingid);
    expect(fetched.response.status()).toBe(200);
    expect(fetched.body).toMatchObject(payload);
  });
});
