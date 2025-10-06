const { test, expect } = require('@playwright/test');
const AxeBuilder = require('@axe-core/playwright').default;

test.describe('Pokédex Application', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('has correct title', async ({ page }) => {
    await expect(page).toHaveTitle(/React App/);
  });

  test('displays Pokédex heading', async ({ page }) => {
    const heading = page.getByRole('heading', { name: /Pokédex/i });
    await expect(heading).toBeVisible();
  });

  test('displays tagline', async ({ page }) => {
    const tagline = page.getByText(/Gotta catch 'em all!/i);
    await expect(tagline).toBeVisible();
  });

  test('has search input', async ({ page }) => {
    const searchInput = page.getByPlaceholder(/Search by name, number, or type/i);
    await expect(searchInput).toBeVisible();
    await expect(searchInput).toBeEditable();
  });

  test('loads and displays Pokemon', async ({ page }) => {
    // Wait for at least one Pokemon link to be visible
    await page.waitForSelector('a[href*="pokemon.com"]', { timeout: 30000 });

    const pokemonLinks = await page.locator('a[href*="pokemon.com"]').count();
    expect(pokemonLinks).toBeGreaterThan(0);
  });

  test('search filters Pokemon', async ({ page }) => {
    // Wait for Pokemon to load
    await page.waitForSelector('a[href*="pokemon.com"]', { timeout: 30000 });

    const searchInput = page.getByPlaceholder(/Search by name, number, or type/i);

    // Search for "pikachu"
    await searchInput.fill('pikachu');
    await page.waitForTimeout(500); // Wait for filter to apply

    const results = await page.locator('a[href*="pokemon.com"]').count();
    expect(results).toBeGreaterThanOrEqual(1);

    // Check if Pikachu is in results
    const pikachuLink = page.getByRole('link', { name: /pikachu/i });
    await expect(pikachuLink).toBeVisible();
  });

  test('Pokemon links open in new tab', async ({ page }) => {
    await page.waitForSelector('a[href*="pokemon.com"]', { timeout: 30000 });

    const firstLink = page.locator('a[href*="pokemon.com"]').first();
    await expect(firstLink).toHaveAttribute('target', '_blank');
    await expect(firstLink).toHaveAttribute('rel', /noopener noreferrer/);
  });
});

test.describe('Dark Mode', () => {
  test('toggles dark mode', async ({ page }) => {
    await page.goto('/');

    // Find dark mode toggle button
    const darkModeButton = page.getByRole('button', { name: /dark/i });
    await expect(darkModeButton).toBeVisible();

    // Click to enable dark mode
    await darkModeButton.click();

    // Check if dark class is added to html element
    const htmlClass = await page.locator('html').getAttribute('class');
    expect(htmlClass).toContain('dark');

    // Button should now show "Light"
    await expect(page.getByRole('button', { name: /light/i })).toBeVisible();
  });

  test('persists dark mode preference', async ({ page, context }) => {
    await page.goto('/');

    // Enable dark mode
    const darkModeButton = page.getByRole('button', { name: /dark/i });
    await darkModeButton.click();

    // Reload page
    await page.reload();

    // Dark mode should still be active
    const htmlClass = await page.locator('html').getAttribute('class');
    expect(htmlClass).toContain('dark');
  });
});

test.describe('Internationalization', () => {
  test('changes language', async ({ page }) => {
    await page.goto('/');

    // Find language selector
    const languageSelect = page.getByRole('combobox', { name: /language/i });
    await expect(languageSelect).toBeVisible();

    // Change to Spanish
    await languageSelect.selectOption('es');

    // Check if tagline changed
    const spanishTagline = page.getByText(/¡Hazte con todos!/i);
    await expect(spanishTagline).toBeVisible();

    // Change to Japanese
    await languageSelect.selectOption('ja');

    // Check if title changed
    const japaneseTitle = page.getByRole('heading', { name: /ポケモン図鑑/i });
    await expect(japaneseTitle).toBeVisible();
  });
});

test.describe('Accessibility', () => {
  test('has no automatically detectable accessibility issues', async ({ page }) => {
    await page.goto('/');

    // Wait for content to load
    await page.waitForLoadState('networkidle');

    const accessibilityScanResults = await new AxeBuilder({ page }).analyze();

    expect(accessibilityScanResults.violations).toEqual([]);
  });

  test('keyboard navigation works', async ({ page }) => {
    await page.goto('/');

    // Tab to search input
    await page.keyboard.press('Tab');

    const searchInput = page.getByPlaceholder(/Search by name, number, or type/i);
    await expect(searchInput).toBeFocused();

    // Type in search
    await page.keyboard.type('bulbasaur');
    await page.waitForTimeout(500);

    // Check that results are filtered
    const results = await page.locator('a[href*="pokemon.com"]').count();
    expect(results).toBeGreaterThanOrEqual(1);
  });
});

test.describe('Responsive Design', () => {
  test('works on mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 }); // iPhone SE
    await page.goto('/');

    await expect(page.getByRole('heading', { name: /Pokédex/i })).toBeVisible();
    await expect(page.getByPlaceholder(/Search by name, number, or type/i)).toBeVisible();
  });

  test('works on tablet viewport', async ({ page }) => {
    await page.setViewportSize({ width: 768, height: 1024 }); // iPad
    await page.goto('/');

    await expect(page.getByRole('heading', { name: /Pokédex/i })).toBeVisible();
    await expect(page.getByPlaceholder(/Search by name, number, or type/i)).toBeVisible();
  });
});
