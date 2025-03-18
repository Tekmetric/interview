import { test, expect } from '@playwright/test';
import { searchProduct } from '../api/apiHelpers';
import { HomePage } from '../pom/HomePage';
import { ModalPage } from '../pom/ModalPage';
import { CartPage } from '../pom/CartPage';  
import { testData } from '../data/testData';

test('Add Product to Cart - API Search + UI Flow', async ({ request, page }) => {
    const homePage = new HomePage(page);   
    const modalPage = new ModalPage(page);
    const cartPage = new CartPage(page);

    await homePage.navigate();

    const searchedProduct = await searchProduct(request, testData.productToSearch);

    await cartPage.addProductToCartApiTest(searchedProduct.id);  
    await modalPage.verifyProductAdded();
    await modalPage.goToCart();
    await cartPage.verifyOnCartPage();
    await cartPage.verifyProductInCartApiTest(searchedProduct.name);  
});
