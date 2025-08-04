import { test, expect, request } from '@playwright/test';
import { NAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, TITLE, COMPANY, COUNTRY, STATE, ADDRESS, CITY,
   ZIPCODE, MOBILE_NUMBER, DOB_DAY, DOB_MONTH, DOB_YEAR
} from './test_data';

// Test for registering a user account

test('register user via API', async () => {
  
  // payload
  const data = {
    name: NAME,
    email: EMAIL,
    password: PASSWORD,
    title: TITLE,
    birth_date: DOB_DAY,
    birth_month: DOB_MONTH,
    birth_year: DOB_YEAR,
    firstname: FIRST_NAME,
    lastname: LAST_NAME,
    company: COMPANY,
    address1: ADDRESS,
    country: COUNTRY,
    zipcode: ZIPCODE,
    state: STATE,
    city: CITY,
    mobile_number: MOBILE_NUMBER
  };

  // Create API request context
  const apiContext = await request.newContext({
    baseURL: 'https://www.automationexercise.com'
  });

  // Sending POST request to register user account
  const response = await apiContext.post('/api/createAccount', {
    form: data
  });

  // Make sure the response is successful
  expect(response.status()).toBe(200);

  // Check if the response contains the expected data (code 201 per API documentation)
  const responseJson = await response.json();
   expect(responseJson.responseCode).toBe(201);
});