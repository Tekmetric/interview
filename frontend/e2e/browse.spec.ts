import { expect, test } from '@playwright/test';

import { mockApi } from './apiMock';

test.beforeEach(async ({ page }) => {
  await mockApi(page);
});

test('browse, search, and follow cross-links between entities', async ({ page }) => {
  await page.goto('/');

  // The home route redirects to the characters list.
  await expect(page).toHaveURL(/\/characters$/);
  await expect(page.getByRole('link', { name: 'Rick Sanchez' })).toBeVisible();

  // Debounced search narrows the results and syncs to the URL.
  await page.getByRole('searchbox', { name: 'Search characters by name' }).fill('birdperson');
  await expect(page).toHaveURL(/name=birdperson/);
  const birdperson = page.getByRole('link', { name: 'Birdperson', exact: true });
  await expect(birdperson).toBeVisible();

  // Character detail with facts and episode appearances.
  await birdperson.click();
  await expect(page.getByRole('heading', { level: 1, name: 'Birdperson' })).toBeVisible();
  await expect(page.getByText('Bird-Person')).toBeVisible();

  // Cross-link into an episode detail.
  await page.getByRole('link', { name: 'Ricksy Business' }).click();
  await expect(page.getByRole('heading', { level: 1, name: 'Ricksy Business' })).toBeVisible();
  await expect(page.getByText('S01E11')).toBeVisible();
});

test('a search with no matches shows the friendly empty state', async ({ page }) => {
  await page.goto('/characters?name=xxnothingmatchesxx');

  await expect(page.getByText('No beings found in this dimension')).toBeVisible();
});

test('the virtualized grid keeps the DOM flat while pages accumulate', async ({ page }) => {
  await page.goto('/characters');
  await expect(page.getByRole('link', { name: 'Rick Sanchez' })).toBeVisible();

  const loadMore = page.getByRole('button', { name: 'Load more characters' });
  for (let round = 0; round < 3; round += 1) {
    await loadMore.click();
    // While fetching, the button is disabled and relabelled "Loading…" —
    // wait for it to settle before the next round.
    await expect(loadMore).toBeEnabled();
  }

  // 80 of 300 characters are loaded; only rows near the viewport are in the DOM.
  await expect(page.getByText('300 characters found')).toBeVisible();
  const renderedCards = await page.getByRole('listitem').count();
  expect(renderedCards).toBeLessThan(40);
});
