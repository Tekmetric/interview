import { test, expect, request } from '@playwright/test';
import { EMAIL, PASSWORD } from './test_data'; 

test('DELETE /api/deleteAccount - delete user account', async () => {

  const data = {
    email: EMAIL,
    password: PASSWORD,
  };

  // Create API request context
  const apiContext = await request.newContext({
    baseURL: 'https://www.automationexercise.com'
  });

  const response = await apiContext.delete('/api/deleteAccount', {
    form: data,
  });

  expect(response.status()).toBe(200);
});
