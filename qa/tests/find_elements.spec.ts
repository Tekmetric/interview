import { test } from '@playwright/test';

test('find book now button', async ({ page }) => {
  await page.goto('https://automationintesting.online/');
  
  // Wait for the page to load
  await page.waitForLoadState('networkidle');

  // Find all buttons that contain "Book"
  const buttons = page.locator('button');
  const count = await buttons.count();
  console.log(`Total buttons found: ${count}`);

  for (let i = 0; i < count; i++) {
    const text = await buttons.nth(i).innerText();
    const isVisible = await buttons.nth(i).isVisible();
    console.log(`Button ${i}: "${text}" (Visible: ${isVisible})`);
    
    if (text.toLowerCase().includes('book')) {
        const className = await buttons.nth(i).getAttribute('class');
        console.log(`- Class: ${className}`);
        // Log the outerHTML to see the structure
        const html = await buttons.nth(i).evaluate(el => el.outerHTML);
        console.log(`- HTML: ${html}`);
    }
  }
});
