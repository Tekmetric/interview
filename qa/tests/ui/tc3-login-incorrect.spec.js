const { test, expect } = require('@playwright/test');

test('TC3: Login User with incorrect credentials', async ({ page }) => {
  console.log('Starting TC3: Login with invalid credentials');
  
  // 1-3. Launch browser and verify home page
  await page.goto('https://www.automationexercise.com/');
  await expect(page).toHaveTitle('Automation Exercise');
  console.log('✓ Home page loaded');

  // 4. Click on 'Signup / Login' button
  await page.click('text=Signup / Login');
  console.log('✓ Clicked Signup/Login');
  
  // 5. Verify 'Login to your account' is visible
  await expect(page.locator('text=Login to your account')).toBeVisible();
  console.log('✓ Login form visible');

  // 6. Enter incorrect email and password
  await page.fill('[data-qa="login-email"]', 'incorrect@example.com');
  await page.fill('[data-qa="login-password"]', 'wrongpassword');
  console.log('✓ Filled invalid credentials');
  
  // 7. Click 'login' button
  await page.click('[data-qa="login-button"]');
  console.log('✓ Clicked login button');
  
  // 8. Verify error message is visible
  await expect(page.locator('text=Your email or password is incorrect!')).toBeVisible();
  console.log('✓ Error message displayed - LOGIN VALIDATION WORKS');
});
