import { expect, test } from '@playwright/test';

import { mockApi } from './apiMock';

test('theme and language choices persist across reloads', async ({ page }) => {
  await mockApi(page);
  await page.goto('/characters');

  const themeToggle = page.getByRole('button', { name: 'Dark theme' });
  await expect(themeToggle).toHaveAttribute('aria-pressed', 'false');

  await themeToggle.click();
  await expect(themeToggle).toHaveAttribute('aria-pressed', 'true');
  // Native UI follows via color-scheme on the root element.
  await expect(page.locator('html')).toHaveCSS('color-scheme', 'dark');

  // Language switch translates the UI and the document language.
  await page.getByRole('combobox', { name: 'Language' }).selectOption('ro-RO');
  await expect(page.getByRole('link', { name: 'Personaje' })).toBeVisible();
  await expect(page.locator('html')).toHaveAttribute('lang', 'ro-RO');

  await page.reload();
  await expect(page.getByRole('button', { name: 'Temă întunecată' })).toHaveAttribute(
    'aria-pressed',
    'true',
  );
  await expect(page.getByRole('link', { name: 'Personaje' })).toBeVisible();
});
