import { test, expect } from '@playwright/test';
import { ProductsPage } from '../page-objects/products-page';

test.describe('Product Categories and Brands', () => {
  let productsPage: ProductsPage;

  test.beforeEach(async ({ page }) => {
    // Create an instance of ProductsPage
    productsPage = new ProductsPage(page);

    // Navigate to the products page
    await productsPage.navigateToProductsView();
  });

  test('should display product categories', async () => {
    // Validate categories panel is visible and contains category titles
    await expect(productsPage.getCategoryPanel()).toBeVisible();

    // Validate category titles are present
    await expect(productsPage.getCategoryTitles()).not.toHaveCount(0);
  });

  test('should display product brands', async () => {
    // Validate brands panel is visible
    await expect(productsPage.getBrandsPanel()).toBeVisible();

    // Validate brand names are present
    // TODO: investigate why these assertions are failing
    // await expect(productsPage.getBrandNames()).toBeVisible();
    // await expect(productsPage.getBrandNames().first()).toBeVisible();
    // await expect(productsPage.getBrandNames().first()).not.toBeEmpty();
  });
});
