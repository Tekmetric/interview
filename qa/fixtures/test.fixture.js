const { test: base, expect } = require('@playwright/test');
const { AutomationPlatformClient } = require('../api/client/automation-platform.client');
const { RestfulBookerClient } = require('../api/client/restful-booker.client');
const { env } = require('../config/env');
const { createGuestDetails, deterministicSuffix } = require('../data/guest.factory');
const { AdminPage } = require('../pages/admin.page');
const { HomePage } = require('../pages/home.page');
const { ReservationPage } = require('../pages/reservation.page');

const test = base.extend({
  homePage: async ({ page }, use) => {
    await use(new HomePage(page));
  },

  reservationPage: async ({ page }, use) => {
    await use(new ReservationPage(page));
  },

  adminPage: async ({ page }, use) => {
    await use(new AdminPage(page));
  },

  automationApi: async ({ playwright }, use) => {
    const request = await playwright.request.newContext({
      baseURL: env.uiBaseUrl,
      extraHTTPHeaders: {
        Accept: 'application/json'
      }
    });

    await use(new AutomationPlatformClient(request));
    await request.dispose();
  },

  restfulBookerApi: async ({ playwright }, use) => {
    const request = await playwright.request.newContext({
      baseURL: env.apiBaseUrl,
      extraHTTPHeaders: {
        Accept: 'application/json',
        'Content-Type': 'application/json'
      }
    });

    await use(new RestfulBookerClient(request));
    await request.dispose();
  },

  availableRoomWindow: async ({ automationApi }, use) => {
    const roomWindow = await automationApi.findAvailableRoomWindow({
      nights: 2,
      startOffsetDays: 30,
      searchDays: 180
    });

    await use(roomWindow);
  },

  guest: async ({}, use, testInfo) => {
    const suffix = deterministicSuffix(testInfo);
    await use(createGuestDetails(suffix));
  }
});

module.exports = {
  test,
  expect
};
