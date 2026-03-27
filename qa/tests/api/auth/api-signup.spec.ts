import { test, expect } from "@playwright/test";
import { AccountApiClient } from "../../../api/AccountApiClient";
import {
  generateUserData,
  InvalidTestData,
} from "../../../utils/test-data-generator";

test.describe("API - Create Account", () => {
  let accountApi: AccountApiClient;
  let userAccountCredentials: { email: string; password: string };

  test.beforeEach(async ({ request }) => {
    accountApi = new AccountApiClient(request);
  });

  test.afterEach(async () => {
    // Only delete if userAccountCredentials was set
    if (userAccountCredentials) {
      await accountApi.safeDeleteAccount(
        userAccountCredentials.email,
        userAccountCredentials.password
      );
    }
  });

  test.describe("POST /api/createAccount - Create User Account", () => {
    test("should return 201 with all required fields @api @happy", async () => {
      // Generate user data
      const user = generateUserData();
      userAccountCredentials = user;

      // Create account
      const response = await accountApi.createAccountFromUserData(user);

      // Assert 201 response
      expect(response.responseCode).toBe(201);

      // Assert response message
      expect(response.message).toBe("User created!");
    });

    test("should return 400 when creating account with existing email @api @unhappy", async () => {
      // Generate user data
      const user = generateUserData();
      userAccountCredentials = user;

      // Create account first time
      const firstResponse = await accountApi.createAccountFromUserData(user);
      expect(firstResponse.responseCode).toBe(201);

      // Try to create with same email
      const secondResponse = await accountApi.createAccountFromUserData(user);

      // Assert 400 response
      expect(secondResponse.responseCode).toBe(400);

      // Assert response message
      expect(secondResponse.message).toBe("Email already exists!");
    });

    test("should return 400 level response when using email with missing '@' @api @unhappy", async () => {
      const user = generateUserData({
        email: InvalidTestData.emails.missingAt, // generates an email missing "@"
      });

      console.log(user.email);

      // Create account using invalid email format
      const response = await accountApi.createAccountFromUserData(user);

      // Assert a non-201 response (should NOT return successful)
      expect(response.responseCode).not.toBe(201);

      // Assert a 400 level response
      expect(response.responseCode).toBeGreaterThanOrEqual(400);
    });

    test("should return 400 when form data is missing required parameters @api @unhappy", async () => {
      const invalidData = {
        name: "Test User",
        email: "test@test.com",
        password: "password123",
        // Missing: firstname, lastname, address1, country, state, city, zipcode, mobile_number
      } as any;

      // Create account without all the required fields (name, email, password only)
      const response = await accountApi.createAccount(invalidData);

      // Assert 400 response
      expect(response.responseCode).toBe(400);

      // Assert response message
      expect(response.message).toBe(
        "Bad request, firstname parameter is missing in POST request."
      );
    });
  });
});
