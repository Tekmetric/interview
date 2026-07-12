import { expect, test } from '@playwright/test';

import { mockApi } from './apiMock';

test('favoriting a character keeps it on the favorites page across reloads', async ({ page }) => {
  await mockApi(page);
  await page.goto('/characters');
  await expect(page.getByRole('link', { name: 'Rick Sanchez' })).toBeVisible();

  // The first card's heart toggle.
  await page.getByRole('button', { name: 'Favorite' }).first().click();

  await page.getByRole('link', { name: 'Favorites' }).click();
  // Wait for the navigation to commit before asserting page content —
  // "Rick Sanchez" would also be visible on the characters page.
  await expect(page).toHaveURL(/\/favorites$/);
  await expect(page.getByRole('heading', { level: 1, name: 'Favorites' })).toBeVisible();
  await expect(page.getByRole('link', { name: 'Rick Sanchez' })).toBeVisible();

  // Persisted through localStorage — survives a full reload.
  await page.reload();
  await expect(page.getByRole('heading', { level: 1, name: 'Favorites' })).toBeVisible();
  await expect(page.getByRole('link', { name: 'Rick Sanchez' })).toBeVisible();

  // Unfavoriting removes it and brings the empty state back.
  await page.getByRole('button', { name: 'Favorite' }).click();
  await expect(page.getByText('Nothing here yet')).toBeVisible();
});
