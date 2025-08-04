import { test, expect, request } from '@playwright/test';
import { EMAIL } from './test_data';

test('get user details by email via API', async () => {

  // Prepare data
  const data = { email: EMAIL };

  // Create API request context
  const apiContext = await request.newContext({
    baseURL: 'https://www.automationexercise.com'
  });

  // Sending GET request to get user details by email
  const response = await apiContext.get('/api/getUserDetailByEmail', {
    params: data
  });

  // Make sure the response is successful
  expect(response.status()).toBe(200);
});