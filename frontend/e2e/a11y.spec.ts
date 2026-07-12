import AxeBuilder from '@axe-core/playwright';
import { expect, test } from '@playwright/test';

import { mockApi } from './apiMock';

test.beforeEach(async ({ page }) => {
  await mockApi(page);
});

// Automated WCAG gate: zero serious or critical axe violations on the key
// screens, in both themes. (Automated checks catch roughly a third of WCAG —
// the rest is covered by the semantics and keyboard behavior asserted in the
// component tests.)
const SCREENS = [
  { name: 'characters list', path: '/characters' },
  { name: 'character detail', path: '/characters/1' },
  { name: 'episodes', path: '/episodes' },
  { name: 'favorites (empty)', path: '/favorites' },
];

for (const screen of SCREENS) {
  test(`${screen.name} has no serious axe violations`, async ({ page }) => {
    await page.goto(screen.path);
    await page.waitForLoadState('networkidle');

    const results = await new AxeBuilder({ page }).analyze();
    const blocking = results.violations.filter(
      (violation) => violation.impact === 'serious' || violation.impact === 'critical',
    );
    expect(blocking.map((v) => ({ id: v.id, impact: v.impact, nodes: v.nodes.length }))).toEqual(
      [],
    );
  });
}

test('characters list passes axe in dark mode', async ({ page }) => {
  await page.goto('/characters');
  await page.getByRole('button', { name: 'Dark theme' }).click();
  await page.waitForLoadState('networkidle');

  const results = await new AxeBuilder({ page }).analyze();
  const blocking = results.violations.filter(
    (violation) => violation.impact === 'serious' || violation.impact === 'critical',
  );
  expect(blocking.map((v) => ({ id: v.id, impact: v.impact }))).toEqual([]);
});

test('the skip link is the first tab stop and moves focus to the content', async ({ page }) => {
  await page.goto('/characters');
  // Wait for React to render before sending keyboard input.
  await expect(page.getByRole('heading', { level: 1, name: 'Characters' })).toBeVisible();

  await page.keyboard.press('Tab');
  await expect(page.getByRole('link', { name: 'Skip to main content' })).toBeFocused();

  await page.keyboard.press('Enter');
  await expect(page.locator('#main')).toBeFocused();
});
