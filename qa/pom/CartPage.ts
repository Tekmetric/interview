import { Page, expect } from '@playwright/test';

export class CartPage {
    constructor(private page: Page) { }

    async verifyOnCartPage() {
        await expect(this.page).toHaveURL('/view_cart');
    }
    async addProductToCartApiTest(productId: string) {
        await this.page.locator(`[data-product-id="${productId}"]`).first().click();
    }
    async verifyProductInCartApiTest(productName: string) {
        await expect(this.page.locator(`td:has-text("${productName}")`)).toBeVisible();
    }
}

