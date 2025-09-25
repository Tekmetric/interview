const { test, expect } = require('@playwright/test');

test('Integration: Use API to support UI test', async ({ page, request }) => {
  console.log('Starting API + UI Integration Test');
  
  // Step 1: Use API to verify products endpoint works
  console.log('1. Testing Products API...');
  const apiResponse = await request.get('https://automationexercise.com/api/productsList');
  expect(apiResponse.status()).toBe(200);
  console.log('✓ Products API is working - Status:', apiResponse.status());
  
  // Step 2: Use UI to verify products page
  console.log('2. Testing Products UI page...');
  await page.goto('https://www.automationexercise.com/');
  await page.click('text=Products');
  await expect(page.locator('text=All Products')).toBeVisible();
  console.log('✓ Products UI page is working');
  
  // Step 3: Combine API knowledge with UI verification
  const productsData = await apiResponse.json();
  if (productsData.products && productsData.products.length > 0) {
    console.log('✓ API confirms', productsData.products.length, 'products available');
    // Verify UI shows products (basic check)
    await expect(page.locator('.product-image-wrapper').first()).toBeVisible();
    console.log('✓ UI correctly displays products');
  }
  
  console.log('🎉 API + UI INTEGRATION TEST COMPLETED SUCCESSFULLY');
});
