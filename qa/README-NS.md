
# Automated Testing Suite

Welcome to the **Automated Testing Suite**. This suite integrates both API and UI tests to ensure robust functionality of core features such as user account management and the purchasing flow.

## 🚀 Overview

This testing suite is developed to cover the following key functionalities:

- **API Tests**: Validate the **Create**, **Update**, and **Delete** operations on user accounts. These tests ensure that backend user management is correctly implemented.

- **UI Tests**: Execute end-to-end scenarios including:
  - **User Login**
  - **Account Creation**
  - **Adding Products to Cart/Checkout Process**

## 📦 Features

- **Modular API Layer**: Developed an API testing module that encapsulates user management operations. This enables reusable and consistent interactions with the backend during tests.

- **Integrated Dotenv for Environment Configuration**: Tests are configured to operate across four distinct environments:
  - **Development**
  - **QA**
  - **Staging**
  - **Production**

  _Each environment is configured in a `.env` file, allowing seamless switching and maintaining of environment-specific variables._

- **Independence and Isolation**: Every test is fully independent, utilizing hooks to set up preconditions (`beforeAll`, `beforeEach`) and perform cleanup operations (`afterAll`, `afterEach`). This ensures that test runs do not interfere with each other and maintain consistent states.

## ⚙️ Environment Setup

To run these tests, ensure that `.env` files are correctly configured with environment-specific API URLs and credentials.

### Sample `.env` File

```ini
DEV_API_URL=https://dev.yourapi.com/api
QA_API_URL=https://qa.yourapi.com/api
STAGING_API_URL=https://staging.yourapi.com/api
PROD_API_URL=https://prod.yourapi.com/api
```

## 🏃‍♂️ Running the Tests

### Prerequisites

- **Node.js** installed on your system
- **Playwright** installed for running UI tests

### Install Playwright

```bash
npm install @playwright/test
npx playwright install
```

### Execute Tests

To target a specific environment, set the `TEST_ENV` variable:

```bash
TEST_ENV=development npx playwright test
```

## 📂 Directory Structure

- `/pageObjects`: Contains classes and methods for interacting with the application UI.
- `/tests`: Includes API and UI test specifications
- `/utils`: Utility functions like email generation and helper functions for environment management
- `/testData`: JSON files with test data.
Certainly! Below is the README section updated with proper markdown formatting for running specific tests, using clear explanations and styled with markdown:

## 🏃‍♂️ Running Specific Tests

### Run a Specific Test File

To run a specific test file, provide the path to the test:

```bash
TEST_ENV=development npx playwright test tests/userAccountAPITests.spec.ts
```

### Run a Specific Test by Name

To run a specific test by name within a test file, use the `-g` or `--grep` option:

```bash
TEST_ENV=development npx playwright test -g "Create/Register User Account"
```

These commands allow you to narrow down test execution to specific scenarios, aiding in focused development and debugging.

