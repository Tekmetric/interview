# QA Automation Assignment 

Playwright automation test for automationexercise.com 

# Instalation

**Install dependencies and browsers:**

npm install
npx playwright install

## Running Tests 

**Signup and Login Tests:**

npm run SignupTest

**Checkout Test:**

npm run CheckoutTest

**API Tests:**

npm run APITest

npx playwright show-report

**All Test:**

npm run AllTest

## Test Coverage

## UI Tests

**Signup_login.spec.ts**
-Account creation with unique timestamp-based emails
-Login Funtionality valitation 


**Checkout.spec.ts**
-Single product checkout flow   
-Multiple products checkout using loop

## API Tests 

**API.spec.ts**
-Account creation and login verification via API
-Test account setup for UI tests (demonstrates API supporting UI automation)
-Account deletion via API


## Test Account
Pre-configured  account for testing:
-Email: lenny@testing.com
-Password: 1234567890

## Goals 
1. Automate Login, Account Creation, Checkout UI - **COMPLETED**
2. Automate Login & Account CRUD API - **COMPLETED**
3. Utilize API to support UI tests - **COMPLETED**
