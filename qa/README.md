# Framework Documentation

## Getting Started
1. Fork the repository
2. Clone it locally
3. Navigate to the `qa` directory
   ```bash
   cd qa
   ```
4. Install dependencies
   ```bash
   npm install
   ```


## Testing Website
https://automationintesting.online/

## Repository
https://github.com/mwinteringham/restful-booker-platform  
https://github.com/mwinteringham/restful-booker  

## API Docs
https://restful-booker.herokuapp.com/apidoc/index.html  



## High-Level Architecture

qa/
├── fixtures/
│   └── index.ts               # Playwright fixture definitions and dependency injection
├── helpers/
│   ├── api-helper.ts          # API request functions (CRUD operations)
│   ├── auth-helper.ts         # Authentication helpers for both services
│   ├── date-helper.ts         # Date utilities for generating future dates
│   └── test-data.ts           # Faker-based test data generators
├── page-objects/
│   ├── components/
│   │   └── contact-component.ts  # Contact form as a scoped component
│   ├── home-page.ts           # Home page interactions
│   └── reservation-page.ts    # Room reservation and calendar interactions
├── tests/
│   ├── api/
│   │   └── booking-crud.spec.ts  # API-level booking CRUD tests
│   └── ui/
│       ├── admin-session.spec.ts  # Admin session reuse test
│       ├── booking-end-to-end.spec.ts  # Full booking flow UI test
│       └── contact-form.spec.ts   # Contact form UI tests
├── global-setup.ts            # One-time admin authentication before test run
├── playwright.config.ts       # Framework configuration
└── storageState.json          # Saved admin session 


1. Tests: this directory contains all the test scripts, separated into ui and api subdirectories for clear distinction between API level and UI level tests.
2. Page Objects: By implementing the Page Object Model design pattern, each page of the application is represented by class with page elements and actions.
3. Helpers: this directory consists of utility functions for tasks like api interactions, authentication, and test data generation
4. Fixtures: this directory extends Playwright's test object with custom ways to set up and tear down test environments, manage page objects, and handle authentication
5. Configuration: is responsible for configuring the test framework by defining settings for test directory, reporters, browsers, and global setup 

## Key Design Decisions and Reasoning

1. Page Objects and Components: Full pages that have their own URL use the Page Object pattern.UI sections that are embedded within a page and do not have their own URL use the Component pattern.This distinction reflects the actual structure of the application and prevents locators from leaking across boundaries.The ContactComponent scopes all its locators to the #contact section, so it can never accidentally match elements elsewhere on the page.
2. Fixtures for Dependency Injection:All test setup flows through Playwright fixtures rather than beforeEach hooks.This means each test declares exactly what it needs through its parameters and nothing more.Fixtures also handle teardown automatically, keeping tests free of cleanup code.The fixture types are split into UIFixtures and APIFixtures to make the separation of concerns explicit and to make it easy to see at a glance what each test category depends on.
3. Single Import for Test Data: All test data generation flows through `test-data.ts.` Tests never import directly from `date-helper.ts` or `faker`. This gives a single place to change data generation logic without hunting across test files, and it makes imports in spec files clean and consistent.
4. Faker for Dynamic Data:Every test that creates data uses Faker to generate unique values per run. This prevents state accumulation across runs — the same dates, names, and booking details are never submitted twice. Date offsets for UI tests are randomised between 60 and 180 days in the future to avoid 409 conflicts from rooms being booked out by repeated test runs targeting the same dates.


## How UI and API Tests Interact
UI and API tests are intentionally independent. API tests target the restful-booker API directly via Playwright's `APIRequestContext` and never open a browser. UI tests target `automationintesting.online` and interact entirely through the browser.

The one deliberate crossover is the admin session test, which authenticates via API and reuses that session in the browser to demonstrate clean state management — not API-assisted UI setup.

The API helpers in `api-helper.ts` are also available as setup utilities for UI tests where needed — creating data via API before verifying it in the UI is faster and removes dependency on UI forms for preconditions.

## Test Data and Date Management

All data generation flows through `test-data.ts` as a single import point for tests.

`generateBookingData()` produces a full `Booking` object with Faker-generated values and accepts optional overrides to fix specific fields while keeping everything else random. `generateGuestDetails()` and `generateContactFormData()` cover UI form data — both generate fresh values on every run.

Dates are returned as structured objects `{year, month, day}` rather than strings so they work for both calendar navigation and API payloads. `formatDate()` handles conversion to `YYYY-MM-DD` when needed. For UI tests, `generateBookingDates()` randomises the offset between 60 and 180 days to avoid 409 conflicts from repeated runs targeting the same dates.

## Known Platform Bug

The confirmation page displays checkout one day later than the date selected via calendar drag. The test asserts correct expected behaviour and fails intentionally — this would be raised as a defect in a real project.

## How to Run Tests Locally
```bash
cd qa
npm install
npx playwright install
 ```
Create a `.env` file in the `qa/` directory for environment variables

```bash
# Run all tests
npm test

# Run UI tests only
npm run test:ui

# Run API tests only
npm run test:api

# Run in headed mode to watch the browser
npm run test:headed

# Run in debug mode with Playwright inspector
npm run test:debug

# Open the HTML report after a run
npm run report
```
The `global-setup.ts` runs automatically before any test suite, authenticates as admin via API, and saves the session to storageState.json. The admin project in `playwright.config.ts` loads this saved state so admin tests never perform a UI login.

## How to Run in CI
The configuration file is already configured for CI behavior via the CI environment variables:
# Example GitHub Actions workflow
```bash
name: Playwright Tests
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20

      - name: Install dependencies
        run: npm ci
        working-directory: qa

      - name: Install Playwright browsers
        run: npx playwright install --with-deps
        working-directory: qa

      - name: Run tests
        run: npm run test:ci
        working-directory: qa
        env:
          BASE_URL: ${{ secrets.BASE_URL }}
          API_BASE_URL: ${{ secrets.API_BASE_URL }}
          ADMIN_USERNAME: ${{ secrets.ADMIN_USERNAME }}
          ADMIN_PASSWORD: ${{ secrets.ADMIN_PASSWORD }}
          RESTFUL_BOOKER_USERNAME: ${{ secrets.RESTFUL_BOOKER_USERNAME }}
          RESTFUL_BOOKER_PASSWORD: ${{ secrets.RESTFUL_BOOKER_PASSWORD }}

      - name: Upload Playwright report
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: playwright-report
          path: qa/playwright-report/
           
 ```
 Secrets are never stored in repo.
## AWhat I Would Improve With More Time


- Visual regression testing: I would add visual regression testing to catch unintended UI changes.
- Accessibility testing: I would integrate accessibility testing to ensure that the application is usable by people with disabilities.
- Performance testing: I would add performance testing to measure the application's response time and resource usage under load.
- More comprehensive test coverage: I would expand the test suite to cover more user scenarios and edge cases.
- Improved reporting: I would integrate a more advanced reporting tool, such as Allure, to provide more detailed and interactive test reports.
- Environment-Specific Configurations: Implement a more robust configuration management solution for handling different test environments (e.g., development, staging, production).
- Refine Test Suits: to allow for more targeted runs with @tag feature
- API Response Validation with JSON Schema to catch any unexpected changes in the API structure
- Test Data Cleanup after each run for avoiding database clutter and potential issues wht data conflicts
- Parallel Test Execution Optimization: to reduce feedback time

Thank You!


