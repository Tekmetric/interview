import { APIRequestContext } from '@playwright/test';
import { API_BASE_URL, BASE_URL, ENDPOINTS, ADMIN_CREDENTIALS, RESTFUL_BOOKER_CREDENTIALS } from '../utils/constants';

export interface BookingRequest {
  firstname: string;
  lastname: string;
  totalprice: number;
  depositpaid: boolean;
  bookingdates: {
    checkin: string;
    checkout: string;
  };
  additionalneeds?: string;
}

export interface BookingResponse {
  bookingid: number;
  booking: BookingRequest;
}

export interface AuthResponse {
  token: string;
}

export class ApiClient {
  constructor(private request: APIRequestContext) {}

  async authenticateForBookingApi(): Promise<string> {
    const response = await this.request.post(`${API_BASE_URL}${ENDPOINTS.AUTH.LOGIN}`, {
      data: {
        username: RESTFUL_BOOKER_CREDENTIALS.USERNAME,
        password: RESTFUL_BOOKER_CREDENTIALS.PASSWORD,
      },
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok()) {
      const errorText = await response.text().catch(() => 'Unknown error');
      throw new Error(`Restful Booker auth failed: ${response.status()} - ${errorText}`);
    }

    const body = await response.json();
    if (body.token && typeof body.token === 'string') {
      return body.token;
    }
    throw new Error('Token not found in Restful Booker auth response');
  }

  async authenticateAdmin(): Promise<string> {
    const response = await this.request.post(`${BASE_URL}${ENDPOINTS.PLATFORM_AUTH.LOGIN}`, {
      data: {
        username: ADMIN_CREDENTIALS.USERNAME,
        password: ADMIN_CREDENTIALS.PASSWORD,
      },
    });

    if (!response.ok()) {
      const errorText = await response.text().catch(() => 'Unknown error');
      throw new Error(`Platform admin auth failed: ${response.status()} - ${errorText}`);
    }

    try {
      const body = await response.json();
      if (body.token && typeof body.token === 'string') {
        return body.token;
      }
    } catch {}

    const responseHeaders = response.headers();
    const setCookieHeader = responseHeaders['set-cookie'];
    if (setCookieHeader) {
      const cookieStrings = Array.isArray(setCookieHeader) ? setCookieHeader : [setCookieHeader];
      for (const cookieString of cookieStrings) {
        if (cookieString.includes('token=')) {
          const tokenMatch = cookieString.match(/token=([^;,\s]+)/);
          if (tokenMatch?.[1]) return tokenMatch[1];
        }
      }
    }
    throw new Error('Token not found in platform auth response');
  }

  async createBooking(booking: BookingRequest): Promise<BookingResponse> {
    const response = await this.request.post(`${API_BASE_URL}${ENDPOINTS.BOOKING.BASE}`, {
      data: booking,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
    });

    if (!response.ok()) {
      throw new Error(`Failed to create booking: ${response.status()}`);
    }

    return await response.json();
  }

  async getBooking(id: number): Promise<BookingRequest> {
    const response = await this.request.get(`${API_BASE_URL}${ENDPOINTS.BOOKING.BY_ID(id)}`, {
      headers: {
        'Accept': 'application/json',
      },
    });

    if (!response.ok()) {
      throw new Error(`Failed to get booking ${id}: ${response.status()}`);
    }

    return await response.json();
  }

  async updateBooking(id: number, booking: Partial<BookingRequest>, token: string): Promise<BookingRequest> {
    const response = await this.request.put(`${API_BASE_URL}${ENDPOINTS.BOOKING.BY_ID(id)}`, {
      data: booking,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Cookie': `token=${token}`,
        'Authorization': 'Basic YWRtaW46cGFzc3dvcmQxMjM=',
      },
    });

    if (!response.ok()) {
      const errText = await response.text().catch(() => '');
      throw new Error(`Failed to update booking ${id}: ${response.status()} - ${errText}`);
    }

    return await response.json();
  }

  async deleteBooking(id: number, token: string): Promise<void> {
    const response = await this.request.delete(`${API_BASE_URL}${ENDPOINTS.BOOKING.BY_ID(id)}`, {
      headers: {
        'Cookie': `token=${token}`,
        'Authorization': 'Basic YWRtaW46cGFzc3dvcmQxMjM=',
      },
    });

    if (!response.ok()) {
      throw new Error(`Failed to delete booking ${id}: ${response.status()}`);
    }
  }

  async getAllBookingIds(): Promise<number[]> {
    const response = await this.request.get(`${API_BASE_URL}${ENDPOINTS.BOOKING.BASE}`, {
      headers: {
        'Accept': 'application/json',
      },
    });

    if (!response.ok()) {
      throw new Error(`Failed to get booking IDs: ${response.status()}`);
    }

    const data = await response.json();
    if (Array.isArray(data)) {
      return data.map((item: { bookingid?: number }) => item?.bookingid ?? item).filter((id: unknown): id is number => typeof id === 'number');
    }
    return [];
  }
}
