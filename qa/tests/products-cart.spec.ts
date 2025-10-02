import { test, expect } from '@playwright/test';
import { CartPage } from '../page-objects/cart-page';
import { ProductsPage } from '../page-objects/products-page';

test.describe('Cart Operations', () => {
  let productsPage: ProductsPage;
  let cartPage: CartPage;

  test.beforeEach(async ({ page }) => {
    productsPage = new ProductsPage(page);
    cartPage = new CartPage(page);
  });

  test('should add a product to cart and then remove it', async ({ page }) => {
    // Navigate and add product
    await productsPage.navigateToProductsView();
    const firstProduct = await productsPage.getAddToCartButtons().first();
    await firstProduct.click();

    // Verify modal with explicit assertion
    await expect(productsPage.getAddedModal()).toBeVisible();
    
    // Navigate to cart
    await cartPage.navigateToCart();

    // Add specific cart validations
    const cartInfo = cartPage.getCartInfo();

    // Validate cart info section is visible and contains expected headers
    await expect(cartInfo).toBeVisible();
    await expect(cartInfo).toContainText('Item');
    await expect(cartInfo).toContainText('Description');
    await expect(cartInfo).toContainText('Price');
    await expect(cartInfo).toContainText('Quantity');
    await expect(cartInfo).toContainText('Total');

    // Validate at least one product is in the cart
    const rowCount = await cartPage.getCartRows().count();
    expect(rowCount).toBeGreaterThan(0);
    
    // Remove product
    await cartPage.removeFirstProductFromCart();

    // Verify empty cart with explicit wait
    await expect(cartPage.getEmptyCartMessageWithLink()).toBeVisible();
  });
});
