package api;

import api.client.AutomationExerciseApiClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UserAccountCrudApiTest {

    private final String baseUrl = "https://automationexercise.com";
    private final AutomationExerciseApiClient api = new AutomationExerciseApiClient(baseUrl);

    @Test
    void userAccount_crud_happyPath() {
        String unique = String.valueOf(Instant.now().toEpochMilli());
        String email = "kate_iqa_" + unique + "@example.com";
        String password = "Rabota12345!";

        Map<String, String> createForm = new HashMap<>();
        createForm.put("name", "Kate QA");
        createForm.put("email", email);
        createForm.put("password", password);
        createForm.put("title", "Ms");
        createForm.put("birth_date", "10");
        createForm.put("birth_month", "1");
        createForm.put("birth_year", "1995");
        createForm.put("firstname", "Kate");
        createForm.put("lastname", "IQA");
        createForm.put("company", "Tekmetric");
        createForm.put("address1", "Test Address 1");
        createForm.put("address2", "Test Address 2");
        createForm.put("country", "Canada");
        createForm.put("zipcode", "12345");
        createForm.put("state", "ON");
        createForm.put("city", "Toronto");
        createForm.put("mobile_number", "1234567890");

        boolean created = false;

        try {
            // 1) Create account
            Response createRes = api.createAccount(createForm);
            assertThat(createRes.statusCode()).isIn(200, 201);
            assertThat(createRes.jsonPath().getString("message"))
                    .containsIgnoringCase("User created");
            created = true;

            System.out.println("=== CREATE ACCOUNT RESPONSE ===");
            System.out.println(createRes.asString());

            // 2) Verify login (valid)
            Response loginRes = api.verifyLogin(email, password);
            assertThat(loginRes.statusCode()).isIn(200, 201);
            assertThat(loginRes.jsonPath().getString("message"))
                    .containsIgnoringCase("User exists");

            // 3) Get user details (pre-update)
            Response detailsRes = api.getUserDetailByEmail(email);
            assertThat(detailsRes.statusCode()).isIn(200, 201);
            assertThat(detailsRes.asString()).contains(email);

            // 4) Update account (city/state)
            Map<String, String> updateForm = new HashMap<>(createForm);
            updateForm.put("city", "Novosibirsk");
            updateForm.put("state", "NS");

            Response updateRes = api.updateAccount(updateForm);
            assertThat(updateRes.statusCode()).isIn(200, 201);
            assertThat(updateRes.jsonPath().getString("message"))
                    .containsIgnoringCase("User updated");

            // 5) Post-condition: update really applied
            Response detailsAfterUpdate = api.getUserDetailByEmail(email);
            assertThat(detailsAfterUpdate.statusCode()).isIn(200, 201);
            assertThat(detailsAfterUpdate.asString()).contains("Novosibirsk");

        } finally {
            // 6) Cleanup
            if (created) {
                Response deleteRes = api.deleteAccount(email, password);
                assertThat(deleteRes.statusCode()).isIn(200, 201);
                assertThat(deleteRes.jsonPath().getString("message"))
                        .containsIgnoringCase("Account deleted");

                System.out.println("=== DELETE ACCOUNT RESPONSE ===");
                System.out.println(deleteRes.asString());

                // 7) Post-condition: user really deleted
                Response loginAfterDelete = api.verifyLogin(email, password);
                System.out.println("=== LOGIN AFTER DELETE RESPONSE ===");
                System.out.println(loginAfterDelete.asString());

                assertThat(loginAfterDelete.jsonPath().getString("message"))
                        .containsIgnoringCase("User not found");
            }
        }
    }
}
