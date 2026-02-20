const ADMIN_ROUTES = {
  rooms: '/admin/rooms',
  login: '/admin'
};

class AdminPage {
  constructor(page) {
    this.page = page;
  }

  async gotoRooms() {
    await this.page.goto(ADMIN_ROUTES.rooms);
  }

  async gotoLogin() {
    await this.page.goto(ADMIN_ROUTES.login);
  }
}

module.exports = {
  AdminPage
};
