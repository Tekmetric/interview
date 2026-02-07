package api;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class VerifyLoginApiTest {
    private final String baseUrl = "https://automationexercise.com";

    @Test
    void verifyLogin_invalidDetails_returns404() {
        Response res = given()
                .baseUri(baseUrl)
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", "no_such_user@example.com")
                .formParam("password", "wrong")
                .when()
                .post("/api/verifyLogin");

        int code = res.statusCode();
        assertThat(code).isIn(200, 404);

        assertThat(res.jsonPath().getString("message"))
                .containsIgnoringCase("User not found");

    }
}
