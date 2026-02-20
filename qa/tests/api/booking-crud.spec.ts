import { faker } from "@faker-js/faker";
import { test, expect } from "../../fixtures";
import {
  createBooking,
  getBookingById,
  updateBookingById,
  deleteBookingById,
} from "../../helpers/api-helper";
import { generateBookingData } from "../../helpers/test-data";

test.describe("Booking API", () => {
  test("should create a new booking", async ({ request }) => {
    const data = generateBookingData();
    const newBooking = await createBooking(request, data);
    const id = newBooking.bookingid;

    expect(id).toBeDefined();
    expect(newBooking.booking.firstname).toBe(data.firstname);
    expect(newBooking.booking.lastname).toBe(data.lastname);
    expect(newBooking.booking.depositpaid).toBe(data.depositpaid);
  });

  test("should get an existing booking by id", async ({ request }) => {
    const created = await createBooking(request);
    const booking = await getBookingById(request, created.bookingid);

    expect(booking.firstname).toBe(created.booking.firstname);
    expect(booking.lastname).toBe(created.booking.lastname);
    expect(booking.bookingdates.checkin).toBe(
      created.booking.bookingdates.checkin,
    );
    expect(booking.bookingdates.checkout).toBe(
      created.booking.bookingdates.checkout,
    );
  });
  test("should update an existing booking", async ({ request, apiToken }) => {
    const created = await createBooking(request);
    const updatedFirstName = faker.person.firstName();
    const updatedLastName = faker.person.lastName();
    const updated = await updateBookingById(
      request,
      created.bookingid,
      apiToken,
      {
        firstname: updatedFirstName,
        lastname: updatedLastName,
      },
    );
    expect(updated.firstname).toBe(updatedFirstName);
    expect(updated.lastname).toBe(updatedLastName);
  });
  test("should delete an existing booking", async ({ request, apiToken }) => {
    const created = await createBooking(request);

    const deleteResponse = await deleteBookingById(
      request,
      created.bookingid,
      apiToken,
    );
    expect(deleteResponse.status()).toBe(201);

    const getResponse = await request.get(
      process.env.API_BASE_URL + `/booking/${created.bookingid}`,
    );
    expect(getResponse.status()).toBe(404);
  });
});
