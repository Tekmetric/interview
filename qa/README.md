# Playwright Automation Framework

## Assignment Goals Completed

### UI Automation
- Login UI: TC3 - Login with invalid credentials
- Account Creation UI: TC1 - Register User
- Checkout UI: TC16 - Checkout navigation flow

### API Automation
- User Account CRUD: API endpoints testing
- API + UI Integration: Combined verification

## Project Structure
tests/
├── ui/
│   ├── tc1-register-user.spec.js
│   ├── tc3-login-incorrect.spec.js
│   └── tc16-checkout-login.spec.js
├── api/
│   └── user-crud-api.spec.js
└── integration/
    └── api-ui-integration.spec.js

## Quick Start
npm install
npx playwright install
npx playwright test
npx playwright show-report
