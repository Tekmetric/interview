import { test, expect } from '@playwright/test';
import { ProductsPage } from '../page-objects/products.page';

test.describe('Search Products', () => {
  test('should search for a product by name', async ({ page }) => {
    const productsPage = new ProductsPage(page);
    await productsPage.navigateToProductsView();
    await productsPage.searchProduct('Dress');
    await expect(productsPage.getProductName().first()).toContainText('Dress');
  });
});
