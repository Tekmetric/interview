# Playwright Automation Framework

## High Level Framework Architecture

This automation framework is built using Playwright with TypeScript, following best practices for maintainability, scalability, and reusability.

### Directory Structure

```
qa/
├── api/                    # API client and helpers
│   └── api-client.ts      # Centralized API client for RESTful Booker API
├── fixtures/               # Playwright fixtures
│   └── test-fixtures.ts   # Custom fixtures for reusable test setup
├── helpers/                # Helper utilities
│   └── session-manager.ts # Session management for authentication reuse
├── pages/                  # Page Object Models (POM)
│   ├── HomePage.ts
│   ├── BookingPage.ts
│   ├── BookingConfirmationPage.ts
│   └── AdminPage.ts
├── tests/                  # Test files
│   ├── ui/                 # UI test suites
│   │   ├── booking-flow.spec.ts
│   │   ├── contact-form.spec.ts
│   └── api/                # API test suites
│       ├── booking-api.spec.ts
│       └── admin-auth.spec.ts
├── utils/                  # Utility functions
│   ├── constants.ts        # Application constants
│   ├── test-data.ts        # Test data generators
│   └── date-helpers.ts     # Date utility functions
├── playwright.config.ts    # Playwright configuration
└── package.json
```

## Key Design Decisions and Reasoning

### 1. Page Object Model (POM) Pattern
- **Why**: Separates test logic from page implementation details
- **Benefit**: Changes to UI only require updates in one place (the page object)
- **Implementation**: Each page has its own class with methods representing user actions

### 2. Custom Fixtures
- **Why**: Provides reusable test setup and shared context
- **Benefit**: Tests are cleaner, setup is consistent, and fixtures can be composed
- **Implementation**: Custom fixtures for API client, authenticated context, and page objects

### 3. Session Management Helper
- **Why**: Implements the bonus requirement for API authentication with browser session reuse
- **Benefit**: Token caching reduces API calls, session reuse eliminates UI login flows
- **Implementation**: `SessionManager` class handles authentication, token caching, and cookie management

### 4. Centralized API Client
- **Why**: Single source of truth for API interactions
- **Benefit**: Consistent error handling, easy to mock in tests, reusable across test suites
- **Implementation**: `ApiClient` class encapsulates all API operations

### 5. Test Data Utilities
- **Why**: Dynamic test data reduces flakiness and improves test independence
- **Benefit**: Tests don't conflict with each other, easier to run in parallel
- **Implementation**: Generators for guest details, dates, and other test data

### 6. Flexible Locators
- **Why**: UI selectors can change, tests should be resilient
- **Benefit**: Tests use multiple selector strategies with fallbacks
- **Implementation**: Page objects use `.or()` chains with multiple selector strategies

## How UI and API Tests Interact

### Separation of Concerns
- **UI Tests**: Focus on user interactions, visual feedback, and end-to-end flows
- **API Tests**: Focus on data integrity, business logic, and contract validation

### Shared Components
- Both test types use the same `ApiClient` for API operations
- Both use the same `SessionManager` for authentication
- Both use the same test data utilities for consistency

### Example Interaction
The bonus test (`admin-auth.spec.ts`) demonstrates how API and UI tests can work together:
1. API authenticates admin and gets token
2. Token is stored in browser context
3. UI test accesses admin page without UI login
4. Both API and UI assertions verify the flow

## Test Data and Date Management

### Test Data Strategy
- **Dynamic Generation**: Uses `@faker-js/faker` for realistic test data
- **Isolation**: Each test generates unique data to avoid conflicts
- **Reusability**: Data generators are centralized in `utils/test-data.ts`

### Date Management
- **Dynamic Dates**: Always uses future dates (tomorrow + N days)
- **Format Consistency**: Dates formatted as `YYYY-MM-DD` for API and form inputs
- **Utilities**: `date-helpers.ts` provides reusable date manipulation functions

### Example
```typescript
const dates = generateBookingDates(); // Returns tomorrow + 2 days
const guest = generateGuestDetails();  // Returns random guest data
```

## How Tests Are Executed Locally

### Prerequisites
```bash
npm install
```

### Running Tests

#### All Tests
```bash
npm test
```

#### UI Tests Only
```bash
npm run test:ui
```

#### API Tests Only
```bash
npm run test:api
```

#### Headed Mode (See Browser)
```bash
npm run test:headed
```

#### Debug Mode
```bash
npm run test:debug
```

#### Generate Test Code
```bash
npm run test:codegen
```

#### View Test Report
```bash
npm run test:report
```

### Test Execution Options
- **Parallel Execution**: Tests run in parallel by default (configurable in `playwright.config.ts`)
- **Retries**: Automatic retries on failure (2 retries in CI, 0 locally)
- **Trace**: Traces collected on first retry for debugging
- **Screenshots**: Captured on failure
- **Videos**: Retained on failure

## How to Run This in CI

### GitHub Actions Example
```yaml
name: Playwright Tests

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]

jobs:
  test:
    timeout-minutes: 60
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - uses: actions/setup-node@v3
      with:
        node-version: 18
    - name: Install dependencies
      run: npm ci
    - name: Install Playwright Browsers
      run: npx playwright install --with-deps
    - name: Run Playwright tests
      run: npm test
    - uses: actions/upload-artifact@v3
      if: always()
      with:
        name: playwright-report
        path: playwright-report/
        retention-days: 30
```

### CI Configuration
- **Workers**: Set to 1 in CI (configurable in `playwright.config.ts`)
- **Retries**: 2 retries in CI environment
- **Artifacts**: Test reports, screenshots, and videos uploaded
- **Timeout**: 60 minutes for full test suite

### Environment Variables
- `CI=true` - Enables CI-specific settings (retries, single worker)
- Can be extended for environment-specific URLs, credentials, etc.

## What Would Be Improved Next With More Time

### 1. Enhanced Error Handling
- **Current**: Basic error handling in API client
- **Improvement**: Retry logic for flaky network calls, better error messages

### 2. Test Data Management
- **Current**: Dynamic generation for each test
- **Improvement**: Test data factories, data cleanup strategies, test data seeding

### 3. Visual Regression Testing
- **Current**: Functional assertions only
- **Improvement**: Screenshot comparison, visual diff detection

### 4. Performance Testing
- **Current**: Not implemented
- **Improvement**: API response time assertions, page load performance metrics

### 5. Test Reporting
- **Current**: HTML reporter
- **Improvement**: Integration with test management tools (TestRail, Jira), custom reporters

### 6. Parallel Execution Optimization
- **Current**: Basic parallel execution
- **Improvement**: Smart test sharding, test dependency management

### 7. API Mocking
- **Current**: Tests hit real API
- **Improvement**: Mock server for faster, more reliable tests

### 8. Accessibility Testing
- **Current**: Not implemented
- **Improvement**: Axe-core integration, accessibility assertions

### 9. Cross-Browser Testing
- **Current**: Configured but not fully tested
- **Improvement**: Browser-specific test suites, compatibility matrix

### 10. Documentation
- **Current**: README and code comments
- **Improvement**: API documentation, contribution guidelines, test writing guide

## Framework Features

### Reusable Components
- **Page Objects**: All UI interactions abstracted into reusable page classes
- **API Client**: Centralized API operations
- **Helpers**: Session management, test data generation
- **Fixtures**: Composable test setup



### Extensibility
The framework is designed to be easily extended:
- Add new page objects in `pages/`
- Add new API methods in `api/api-client.ts`
- Add new helpers in `helpers/`
- Add new test utilities in `utils/`
- Compose fixtures for new test scenarios

## Usage Examples

### Writing a New UI Test
```typescript
import { test, expect } from '../fixtures/test-fixtures';

test('my new test', async ({ homePage, bookingPage }) => {
  await homePage.goto();
  // ... test steps
});
```

### Writing a New API Test
```typescript
import { test, expect } from '../fixtures/test-fixtures';

test('my new API test', async ({ apiClient }) => {
  const result = await apiClient.someMethod();
  expect(result).toBeDefined();
});
```

### Using Session Manager
```typescript
import { SessionManager } from '../helpers/session-manager';

const token = await SessionManager.authenticateAdmin(apiClient);
await SessionManager.setAuthenticatedSession(context, token);
```

## Troubleshooting

### Tests Failing Due to Selectors
- Use Playwright's codegen: `npm run test:codegen`
- Check browser DevTools for actual selectors
- Update page objects with correct selectors

### API Tests Failing
- Verify API base URL is correct
- Check authentication credentials
- Review API response in test output

### Session Not Working
- Verify token cookie domain matches site domain
- Check cookie expiration
- Clear token cache: `SessionManager.clearTokenCache()`
