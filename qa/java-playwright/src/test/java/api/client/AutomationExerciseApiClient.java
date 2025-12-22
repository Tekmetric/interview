package api.client;

import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AutomationExerciseApiClient {
    private final String baseUrl;

    public AutomationExerciseApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response verifyLogin(String email, String password) {
        return given()
                .baseUri(baseUrl)
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", email)
                .formParam("password", password)
                .when()
                .post("/api/verifyLogin");
    }

    public Response verifyLoginMissingEmail(String password) {
        return given()
                .baseUri(baseUrl)
                .contentType("application/x-www-form-urlencoded")
                .formParam("password", password)
                .when()
                .post("/api/verifyLogin");
    }

    public Response createAccount(Map<String, String> form) {
        return given()
                .baseUri(baseUrl)
                .contentType("application/x-www-form-urlencoded")
                .formParams(form)
                .when()
                .post("/api/createAccount");
    }

    public Response updateAccount(Map<String, String> form) {
        return given()
                .baseUri(baseUrl)
                .contentType("application/x-www-form-urlencoded")
                .formParams(form)
                .when()
                .put("/api/updateAccount");
    }

    public Response deleteAccount(String email, String password) {
        return given()
                .baseUri(baseUrl)
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", email)
                .formParam("password", password)
                .when()
                .delete("/api/deleteAccount");
    }

    public Response getUserDetailByEmail(String email) {
        return given()
                .baseUri(baseUrl)
                .queryParam("email", email)
                .when()
                .get("/api/getUserDetailByEmail");
    }
}
