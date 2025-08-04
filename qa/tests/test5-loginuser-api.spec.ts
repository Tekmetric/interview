import { test, expect, request } from '@playwright/test';
import { EMAIL, PASSWORD } from './test_data';

test('login user via API', async () => {
  const data = {
    email: EMAIL,
    password: PASSWORD
  };

  const apiContext = await request.newContext({
    baseURL: 'https://www.automationexercise.com'
  });

  const response = await apiContext.post('/api/verifyLogin', {
    form: data
  });
 
  expect(response.status()).toBe(200);
  });