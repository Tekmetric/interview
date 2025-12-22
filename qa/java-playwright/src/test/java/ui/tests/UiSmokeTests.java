package ui.tests;

import api.client.AutomationExerciseApiClient;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import ui.base.BaseUiTest;
import ui.pages.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UiSmokeTests extends BaseUiTest {

    private final String baseUrl = "https://automationexercise.com";
    private final AutomationExerciseApiClient api = new AutomationExerciseApiClient(baseUrl);

    private Map<String, String> buildCreateUserForm(String email, String password) {
        Map<String, String> form = new HashMap<>();
        form.put("name", "Kate QA");
        form.put("email", email);
        form.put("password", password);
        form.put("title", "Ms");
        form.put("birth_date", "10");
        form.put("birth_month", "1");
        form.put("birth_year", "1995");
        form.put("firstname", "Kate");
        form.put("lastname", "IQA");
        form.put("company", "Tekmetric");
        form.put("address1", "Test Address 1");
        form.put("address2", "Test Address 2");
        form.put("country", "Canada");
        form.put("zipcode", "12345");
        form.put("state", "ON");
        form.put("city", "Toronto");
        form.put("mobile_number", "1234567890");
        return form;
    }

    @Test
    void login_valid_userCreatedByApi_showsLoggedInBanner() {
        String unique = String.valueOf(Instant.now().toEpochMilli());
        String email = "kate_ui_" + unique + "@example.com";
        String password = "Pass12345!";

        boolean created = false;
        try {
            Response createRes = api.createAccount(buildCreateUserForm(email, password));
            assertThat(createRes.statusCode()).isIn(200, 201);
            created = true;

            LoginPage loginPage = new LoginPage(page);
            loginPage.open();
            loginPage.login(email, password);

            PlaywrightAssertions.assertThat(page.locator("a:has-text('Logged in as')")).isVisible();

        } finally {
            if (created) {
                api.deleteAccount(email, password);
            }
        }
    }

    @Test
    void login_invalid_showsErrorMessage() {
        String email = "no_such_user_" + Instant.now().toEpochMilli() + "@example.com";
        String password = "wrong_pass";

        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.login(email, password);

        PlaywrightAssertions.assertThat(page.locator("text=Your email or password is incorrect!"))
                .isVisible();
    }

    @Test
    void checkout_loginBeforeCheckout_placesOrderSuccessfully_userCreatedByApi() {
        String unique = String.valueOf(Instant.now().toEpochMilli());
        String email = "kate_ui_" + unique + "@example.com";
        String password = "Pass12345!";

        boolean created = false;

        try {
            // API precondition
            Response createRes = api.createAccount(buildCreateUserForm(email, password));
            assertThat(createRes.statusCode()).isIn(200, 201);
            created = true;

            // UI login
            LoginPage loginPage = new LoginPage(page);
            loginPage.open();
            loginPage.login(email, password);
            PlaywrightAssertions.assertThat(page.locator("a:has-text('Logged in as')")).isVisible();

            // Add product -> Cart -> Checkout
            ProductsPage products = new ProductsPage(page);
            products.open();
            products.openFirstProduct();

            ProductDetailsPage pdp = new ProductDetailsPage(page);
            pdp.addToCartAndOpenCart();

            CartPage cart = new CartPage(page);
            cart.assertCartVisible();
            cart.proceedToCheckout();

            CheckoutPage checkout = new CheckoutPage(page);
            checkout.assertCheckoutVisible();
            checkout.addCommentAndPlaceOrder("QA automation: Playwright + API precondition");

            // Payment
            PaymentPage payment = new PaymentPage(page);
            payment.payAndConfirm("Kate QA", "4111111111111111", "123", "12", "2030");
            payment.assertOrderConfirmed();

        } finally {
            if (created) {
                api.deleteAccount(email, password);
            }
        }
    }
}
