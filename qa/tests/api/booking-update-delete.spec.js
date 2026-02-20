const { test, expect } = require('../../fixtures/test.fixture');
const { buildBookingPayload } = require('../../data/booking.factory');
const { env } = require('../../config/env');

test.describe('API Booking - Update and Delete', () => {
  test('updates existing booking and deletes it with auth token', async ({ restfulBookerApi }, testInfo) => {
    const initialPayload = buildBookingPayload(`U${testInfo.parallelIndex}`);
    const created = await restfulBookerApi.createBooking(initialPayload);
    expect(created.response.status()).toBe(200);

    const auth = await restfulBookerApi.createToken(env.apiUsername, env.apiPassword);
    expect(auth.response.status()).toBe(200);
    expect(auth.body.token).toBeTruthy();

    const updatedPayload = {
      ...initialPayload,
      firstname: `Updated${testInfo.parallelIndex}`,
      additionalneeds: 'Late Checkout'
    };

    const updated = await restfulBookerApi.updateBooking(
      created.body.bookingid,
      updatedPayload,
      auth.body.token
    );

    expect(updated.response.status()).toBe(200);
    expect(updated.body).toMatchObject(updatedPayload);

    const deleted = await restfulBookerApi.deleteBooking(created.body.bookingid, auth.body.token);
    expect(deleted.status()).toBe(201);

    const fetchedAfterDelete = await restfulBookerApi.getBooking(created.body.bookingid);
    expect(fetchedAfterDelete.response.status()).toBe(404);
  });
});
