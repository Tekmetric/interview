const { addDays, buildBookingWindow, toIsoDate } = require('../../helpers/date-helper');

class AutomationPlatformClient {
  constructor(request) {
    this.request = request;
  }

  async getRooms() {
    const response = await this.request.get('/api/room');
    const body = await response.json();
    return body.rooms;
  }

  async getRoomUnavailability(roomId) {
    const response = await this.request.get(`/api/report/room/${roomId}`);
    return response.json();
  }

  async createAdminToken(username, password) {
    const response = await this.request.post('/api/auth/login', {
      data: { username, password }
    });

    if (!response.ok()) {
      return { status: response.status() };
    }

    const body = await response.json();
    return { status: response.status(), token: body.token };
  }

  async findAvailableRoomWindow(options) {
    const rooms = await this.getRooms();
    const roomIds = options?.roomIds || rooms.map((room) => room.roomid);
    const nights = options?.nights || 2;
    const startOffsetDays = options?.startOffsetDays || 30;
    const searchDays = options?.searchDays || 120;
    const startDate = addDays(new Date(), startOffsetDays);

    for (const roomId of roomIds) {
      const unavailableRanges = await this.getRoomUnavailability(roomId);

      for (let dayOffset = 0; dayOffset < searchDays; dayOffset += 1) {
        const checkinDate = addDays(startDate, dayOffset);
        const checkinIso = toIsoDate(checkinDate);
        const window = buildBookingWindow(checkinIso, nights);

        const intersects = unavailableRanges.some((range) => {
          return !(window.checkoutIso <= range.start || window.checkinIso >= range.end);
        });

        if (!intersects) {
          return { roomId, ...window };
        }
      }
    }

    throw new Error('No available room window found in configured search range');
  }
}

module.exports = {
  AutomationPlatformClient
};
