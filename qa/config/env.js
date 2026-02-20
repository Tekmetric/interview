const env = {
  uiBaseUrl: process.env.UI_BASE_URL || 'https://automationintesting.online',
  apiBaseUrl: process.env.API_BASE_URL || 'https://restful-booker.herokuapp.com',
  adminUsername: process.env.ADMIN_USERNAME || 'admin',
  adminPassword: process.env.ADMIN_PASSWORD || 'password',
  apiUsername: process.env.API_USERNAME || 'admin',
  apiPassword: process.env.API_PASSWORD || 'password123'
};

module.exports = { env };
