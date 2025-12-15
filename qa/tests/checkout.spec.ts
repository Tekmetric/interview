import { test, expect } from '@playwright/test';
import { HomePage } from '../page/home_page'
import { LoginPage } from '../page/login_page';
import { ProductPage } from '../page/product_page';
import { CartPage } from '../page/cart_page';
import { CheckoutPage } from '../page/checkout_page';
import { RegisterHelper } from '../lib/register_helper';

// Adds a product to cart, proceeds to checkout, logs in, and places an order.
test('Checkout', async ({ page }) => {
    const home_page = new HomePage(page);
    const login_page = new LoginPage(page);
    const product_page = new ProductPage(page);
    const cart_page = new CartPage(page);
    const checkout_page = new CheckoutPage(page);
    const register_helper = new RegisterHelper();

    // Go to automation exercise website.
    await home_page.goto();

    // Navigate to Products page.
    await home_page.products.click();

    // Add the first product from product list page to cart then view product.
    await product_page.first_modal_add_to_cart_button.click();
    await product_page.first_modal_view_product.click();

    // Add the product to cart from product display page then proceed to cart.
    await product_page.product_page_add_to_cart_button.click();
    await product_page.modal_view_cart_button.click();

    // Assert that cart page is displayed and proceed to checkout.
    await expect(cart_page.proceed_to_checkout_button).toBeVisible();
    await cart_page.proceed_to_checkout_button.click();

    // Assert that user is prompted to log in before checkout then proceed to login.
    await expect(cart_page.modal_register_login_button).toBeVisible();
    await cart_page.modal_register_login_button.click();

    // Create an account via API and log in.
    await register_helper.api_create_account();
    await login_page.login(register_helper.email, register_helper.password);

    // Proceed to Cart Page and then to Checkout.
    await home_page.cart.click();
    await cart_page.proceed_to_checkout_button.click();
    await checkout_page.place_order_button.click();

    // Fill in payment details and place the order.
    await checkout_page.card_name.fill("QA Tester");
    await checkout_page.card_number.fill("4242424242424242");
    await checkout_page.cvc.fill("123");
    await checkout_page.expiry_month.fill("12");
    await checkout_page.expiry_year.fill("2026");
    await checkout_page.pay_and_confirm_order_button.click();

    // Assert that order is placed successfully.
    await expect(checkout_page.order_placed_text).toContainText('Order Placed!');
    await checkout_page.continue_button.click();
    await expect(home_page.logout).toBeVisible();  
    
});
