import { APIRequestContext, APIResponse } from '@playwright/test';
import { generateBookingData } from './test-data';

export interface BookingDates {
  checkin: string;
  checkout: string;
}

export interface Booking {
  firstname: string;
  lastname: string;
  totalprice: number;
  depositpaid: boolean;
  bookingdates: BookingDates;
  additionalneeds?: string;
}

export interface CreatedBooking {
  bookingid: number;
  booking: Booking;
}

/**
 * Creates a new booking in the API
 * @param request
 * @param overrides
 * @returns response created booking with id
 */

export async function createBooking(
  request: APIRequestContext,
  overrides: Partial<Booking> = {},
): Promise<CreatedBooking> {
  const payload: Booking = generateBookingData(overrides);

  const res = await request.post(process.env.API_BASE_URL + '/booking', {
    data: payload,
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
    },
  });
  return res.json() as Promise<CreatedBooking>;
}

/**
 *
 * @param request
 * @param bookingId
 * @returns response with the booking
 */
export async function getBookingById(
  request: APIRequestContext,
  bookingId: number,
): Promise<Booking> {
  const res = await request.get(
    process.env.API_BASE_URL + `/booking/${bookingId}`,
    {
      headers: {
        Accept: 'application/json',
      },
    },
  );
  return res.json() as Promise<Booking>;
}

/**
 * Deletes a booking from the API. Requires an autorization token to be set in the header or a Basic auth header
 * @param request
 * @param bookingId
 * @param token
 */
export async function deleteBookingById(
  request: APIRequestContext,
  bookingId: number,
  token: string,
): Promise<APIResponse> {
  return request.delete(process.env.API_BASE_URL + `/booking/${bookingId}`, {
    headers: {
      'Content-Type': 'application/json',
      Cookie: `token=${token}`,
    },
  });
}

/**
 * Updates current booking
 * @param request
 * @param bookingId
 * @param token
 * @param overrides
 * @returns response with updated booking info
 */
export async function updateBookingById(
  request: APIRequestContext,
  bookingId: number,
  token: string,
  overrides: Partial<Booking> = {},
): Promise<Booking> {
  const currentBooking = await getBookingById(request, bookingId);

  const payload: Booking = {
    ...currentBooking,
    ...overrides,
  };

  const res = await request.put(
    process.env.API_BASE_URL + `/booking/${bookingId}`,
    {
      data: payload,
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
        Cookie: `token=${token}`,
      },
    },
  );

  return res.json() as Promise<Booking>;
}
