# Testing Website:
- For the website under test utilize https://www.automationexercise.com/

# Initialize Playwright:
- Run the following command in your project directory:
  npm init playwright@latest

# Run the specific test:
  npx playwright test tests/test1-registeruser-ui.spec.ts
  npx playwright test tests/test2-loginuser-ui.spec.ts
  npx playwright test tests/test3-checkout-ui.spec.ts
  npx playwright test tests/test4-registeruser-api.spec.ts
  npx playwright test tests/test5-loginuser-api.spec.ts
  npx playwright test tests/test6-userdata-api.spec.ts
  npx playwright test tests/test7-deleteuser-api.spec.ts
  npx playwright test tests/test8_updateAccount-api.spec.ts

# Or to run all tests:
    npx playwright test
