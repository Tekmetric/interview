package ui.tests;

import api.client.AutomationExerciseApiClient;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import ui.base.BaseUiTest;
import ui.pages.LoginPage;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginUiWithApiPreconditionTest extends BaseUiTest {

    private final String baseUrl = "https://automationexercise.com";
    private final AutomationExerciseApiClient api = new AutomationExerciseApiClient(baseUrl);

    @Test
    void login_via_ui_with_user_created_by_api() {
        String unique = String.valueOf(Instant.now().toEpochMilli());
        String email = "kate_ui_" + unique + "@example.com";
        String password = "Pass12345!";

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

        boolean created = false;

        try {
            Response createRes = api.createAccount(form);
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
}
