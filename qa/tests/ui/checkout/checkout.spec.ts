import { test, expect } from "@playwright/test";
import { Navbar } from "../../../pages/Navbar";
import { LoginSignupPage } from "../../../pages/LoginSignupPage";
import { generateUserData } from "../../../utils/test-data-generator";
import { AccountApiClient } from "../../../api/AccountApiClient";
import { ProductsPage } from "../../../pages/ProductsPage";
import { CartPage } from "../../../pages/CartPage";
import { CheckoutPage } from "../../../pages/CheckoutPage";
import { PaymentPage } from "../../../pages/PaymentPage";

test.describe("e2e - checkout", () => {
  let navbar: Navbar;
  let loginSignupPage: LoginSignupPage;
  let productsPage: ProductsPage;
  let cartPage: CartPage;
  let checkoutPage: CheckoutPage;
  let paymentPage: PaymentPage;
  let accountApi: AccountApiClient;
  let userAccountCredentials: { email: string; password: string };

  test.beforeEach(async ({ page, request }) => {
    navbar = new Navbar(page);
    loginSignupPage = new LoginSignupPage(page);
    productsPage = new ProductsPage(page);
    cartPage = new CartPage(page);
    checkoutPage = new CheckoutPage(page);
    paymentPage = new PaymentPage(page);
    accountApi = new AccountApiClient(request);

    // Navigate to homepage
    await page.goto("/");

    // Click 'Signup / Login' link
    await navbar.signupLoginLink.click();
  });

  test.afterEach(async ({ page }) => {
    // Delete the account via API
    await accountApi.safeDeleteAccount(
      userAccountCredentials.email,
      userAccountCredentials.password
    );
  });

  test("should add a single product to cart and checkout successfully @checkout @happy", async ({
    page,
  }) => {
    // Create user data
    const user = generateUserData();
    userAccountCredentials = user;

    // Create account via API
    await accountApi.createAccountFromUserData(user);

    // Log in with valid email and password
    await loginSignupPage.login(user.email, user.password);

    // Click 'Products' link
    await navbar.productsLink.click();

    // Add a random product to the cart
    const product = await productsPage.hoverAndAddRandomProductToCart();

    // Click the 'View Cart' link
    await page.getByRole("link", { name: "View Cart" }).click();

    // Assert redirect to cart page
    await expect(page).toHaveURL("/view_cart");

    // Assert there is one product row
    expect(await cartPage.getCartItemCount()).toBe(1);

    // Assert that the product details in the cart matches the product selected on products page
    await cartPage.verifyProductInCart(product);

    // Click the 'Proceed To Checkout' link
    await cartPage.proceedToCheckoutButton.click();

    // Assert redirect to checkout page
    await expect(page).toHaveURL("/checkout");

    /**
     * NOTE: there may be a bug here due to there being a "." that appears before the
     * user's name on both the delivery and billing address.
     */
    // Assert 'Delivery Address" matches user data
    await checkoutPage.verifyDeliveryAddress(user);

    // Assert that the product details in the checkout page matches the product selected on products page
    await checkoutPage.verifyProductInCheckout(product);

    // Click 'Place Order' button
    await checkoutPage.placeOrderButton.click();

    // Assert redirect to payment page
    await expect(page).toHaveURL("/payment");

    // Fill out payment form and confirm payment
    await paymentPage.completePayment(user);

    // There is a "Your order has been placed successfully!" message that appears below the form just before the page redirects.

    // Assert redirect to order confiration page (total payment amount appended to URL)
    await expect(page).toHaveURL(`/payment_done/${product.priceNumber}`);

    // Assert 'Order Placed!' heading is visible
    await expect(
      page.getByRole("heading", { name: "Order Placed!" })
    ).toBeVisible();

    // Assert 'Congratulations!...' text is visible
    await expect(
      page.getByText("Congratulations! Your order has been confirmed!")
    ).toBeVisible();

    // Assert 'Download Invoice' link (looks like button) is visible
    await expect(
      page.getByRole("link", { name: "Download Invoice" })
    ).toBeVisible();

    // Assert 'Continue' link (looks like button) is enabled
    await expect(page.getByRole("link", { name: "Continue" })).toBeVisible();
  });
});
