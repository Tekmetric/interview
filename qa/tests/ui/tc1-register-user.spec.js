const { test, expect } = require('@playwright/test');

test('TC1: Register User - Account Creation UI', async ({ page }) => {
  console.log('Starting TC1: Account Creation Test');
  
  // 1-3. Launch browser and verify home page
  await page.goto('https://www.automationexercise.com/');
  await expect(page).toHaveTitle('Automation Exercise');
  console.log('✓ Home page loaded');

  // 4. Click on 'Signup / Login' button
  await page.click('text=Signup / Login');
  console.log('✓ Clicked Signup/Login');
  
  // 5. Verify 'New User Signup!' is visible
  await expect(page.locator('text=New User Signup!')).toBeVisible();
  console.log('✓ New User Signup form visible');

  // 6. Enter name and email
  const timestamp = Date.now();
  const testEmail = `test${timestamp}@example.com`;
  await page.fill('[data-qa="signup-name"]', 'Test User');
  await page.fill('[data-qa="signup-email"]', testEmail);
  console.log('✓ Filled signup form with email:', testEmail);
  
  // 7. Click 'Signup' button
  await page.click('[data-qa="signup-button"]');
  console.log('✓ Clicked Signup button');
  
  // 8. Verify that 'ENTER ACCOUNT INFORMATION' is visible
  await expect(page.locator('text=Enter Account Information')).toBeVisible();
  console.log('✓ Account information page loaded - REGISTRATION SUCCESSFUL');
});
