class RestfulBookerClient {
  constructor(request) {
    this.request = request;
  }

  async createToken(username, password) {
    const response = await this.request.post('/auth', {
      data: { username, password }
    });

    const body = await response.json();
    return { response, body };
  }

  async createBooking(payload) {
    const response = await this.request.post('/booking', { data: payload });
    const body = await response.json();
    return { response, body };
  }

  async getBooking(bookingId) {
    const response = await this.request.get(`/booking/${bookingId}`);
    const contentType = response.headers()['content-type'] || '';
    const body = contentType.includes('application/json') ? await response.json() : await response.text();
    return { response, body };
  }

  async updateBooking(bookingId, payload, token) {
    const response = await this.request.put(`/booking/${bookingId}`, {
      data: payload,
      headers: {
        Cookie: `token=${token}`
      }
    });

    const body = await response.json();
    return { response, body };
  }

  async deleteBooking(bookingId, token) {
    return this.request.delete(`/booking/${bookingId}`, {
      headers: {
        Cookie: `token=${token}`
      }
    });
  }
}

module.exports = {
  RestfulBookerClient
};
