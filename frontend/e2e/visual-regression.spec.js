const { test, expect } = require('@playwright/test');

test.describe('Visual Regression Tests', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    // Wait for content to load
    await page.waitForLoadState('networkidle');
  });

  test('homepage light mode', async ({ page }) => {
    await expect(page).toHaveScreenshot('homepage-light.png', {
      fullPage: true,
      mask: [
        // Mask dynamic content that changes (Pokemon images from API)
        page.locator('img[alt*="sprite"]')
      ],
    });
  });

  test('homepage dark mode', async ({ page }) => {
    // Enable dark mode
    const darkModeButton = page.getByRole('button', { name: /dark/i });
    await darkModeButton.click();
    await page.waitForTimeout(500); // Wait for transition

    await expect(page).toHaveScreenshot('homepage-dark.png', {
      fullPage: true,
      mask: [
        page.locator('img[alt*="sprite"]')
      ],
    });
  });

  test('search results', async ({ page }) => {
    await page.waitForSelector('a[href*="pokemon.com"]', { timeout: 30000 });

    const searchInput = page.getByPlaceholder(/Search by name, number, or type/i);
    await searchInput.fill('pikachu');
    await page.waitForTimeout(500);

    await expect(page).toHaveScreenshot('search-results.png', {
      fullPage: true,
      mask: [
        page.locator('img[alt*="sprite"]')
      ],
    });
  });

  test('mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.waitForSelector('a[href*="pokemon.com"]', { timeout: 30000 });

    await expect(page).toHaveScreenshot('mobile-view.png', {
      fullPage: true,
      mask: [
        page.locator('img[alt*="sprite"]')
      ],
    });
  });

  test('tablet viewport', async ({ page }) => {
    await page.setViewportSize({ width: 768, height: 1024 });
    await page.waitForSelector('a[href*="pokemon.com"]', { timeout: 30000 });

    await expect(page).toHaveScreenshot('tablet-view.png', {
      fullPage: true,
      mask: [
        page.locator('img[alt*="sprite"]')
      ],
    });
  });

  test('language spanish', async ({ page }) => {
    const languageSelect = page.getByRole('combobox', { name: /language/i });
    await languageSelect.selectOption('es');
    await page.waitForTimeout(300);

    await expect(page).toHaveScreenshot('language-spanish.png', {
      fullPage: false,
      clip: { x: 0, y: 0, width: 800, height: 400 },
      mask: [
        page.locator('img[alt*="sprite"]')
      ],
    });
  });

  test('component - header light mode', async ({ page }) => {
    const header = page.locator('div[class*="gradient"]').first();

    await expect(header).toHaveScreenshot('header-light.png');
  });

  test('component - header dark mode', async ({ page }) => {
    const darkModeButton = page.getByRole('button', { name: /dark/i });
    await darkModeButton.click();
    await page.waitForTimeout(500);

    const header = page.locator('div[class*="gradient"]').first();

    await expect(header).toHaveScreenshot('header-dark.png');
  });
});

test.describe('Visual Regression - Loading States', () => {
  test('loading state', async ({ page }) => {
    // Intercept API calls to delay them
    await page.route('**/api/v2/pokemon/**', async route => {
      await new Promise(resolve => setTimeout(resolve, 10000));
      await route.continue();
    });

    await page.goto('/');

    // Should show loading state
    await expect(page.getByText(/Loading/i)).toBeVisible();

    await expect(page).toHaveScreenshot('loading-state.png', {
      animations: 'disabled',
    });
  });
});

test.describe('Visual Regression - Error States', () => {
  test('error state', async ({ page }) => {
    // Intercept API and return error
    await page.route('**/api/v2/pokemon/**', route =>
      route.abort('failed')
    );

    await page.goto('/');

    // Wait for error state
    await page.waitForSelector('text=/API Error/i', { timeout: 10000 });

    await expect(page).toHaveScreenshot('error-state.png');
  });
});
