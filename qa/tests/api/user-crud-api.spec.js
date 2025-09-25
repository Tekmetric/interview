const { test, expect } = require('@playwright/test');

test('API: Verify login endpoint', async ({ request }) => {
  console.log('Testing login API endpoint');
  const response = await request.post('https://automationexercise.com/api/verifyLogin', {
    data: {
      email: 'test@example.com',
      password: 'test123'
    }
  });
  expect(response.status()).toBe(200);
  console.log('✓ Login API endpoint working - Status:', response.status());
});

test('API: Get products list', async ({ request }) => {
  console.log('Testing products list API');
  const response = await request.get('https://automationexercise.com/api/productsList');
  expect(response.status()).toBe(200);
  console.log('✓ Products API working - Status:', response.status());
});

test('API: Search product', async ({ request }) => {
  console.log('Testing search product API');
  const response = await request.post('https://automationexercise.com/api/searchProduct', {
    data: {
      search_product: 'jeans'
    }
  });
  expect(response.status()).toBe(200);
  console.log('✓ Search API working - Status:', response.status());
});
