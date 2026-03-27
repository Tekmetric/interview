import { test, expect } from "@playwright/test";
import { AccountApiClient } from "../../../api/AccountApiClient";
import { generateUserData } from "../../../utils/test-data-generator";

test.describe("API - Verify Login", () => {
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

  test.describe("POST /api/verifyLogin - Verify Login Credentials", () => {
    test("should return 200 with valid user credentials @api @happy", async ({
      request,
    }) => {
      // Generate user data
      const user = generateUserData();
      userAccountCredentials = user;

      // Create account
      await accountApi.createAccountFromUserData(user);

      // Call verify login API with valid user credentials
      const response = await accountApi.verifyLoginApi(
        user.email,
        user.password
      );

      // Assert 200 response
      expect(response.responseCode).toBe(200);

      // Assert response message
      expect(response.message).toBe("User exists!");
    });

    test("should return 404 with non-existant user credentials @api @unhappy", async ({
      request,
    }) => {
      // Generate user data
      const user = generateUserData();
      userAccountCredentials = user;

      // Pass non-existant user credentials to verify login API
      const response = await accountApi.verifyLoginApi(
        user.email,
        user.password
      );

      // Assert 404 response
      expect(response.responseCode).toBe(404);

      // Assert response message
      expect(response.message).toBe("User not found!");
    });

    test("should return 404 with invalid user password @api @unhappy", async ({
      request,
    }) => {
      // Generate user data
      const user = generateUserData();
      userAccountCredentials = user;

      // Create account
      await accountApi.createAccountFromUserData(user);

      // Pass invalid user password to verify login API
      const response = await accountApi.verifyLoginApi(
        user.email,
        "wrongpassword"
      );

      // Assert 404 response
      expect(response.responseCode).toBe(404);

      // Assert response message
      expect(response.message).toBe("User not found!");
    });

    test("should return 404 with empty credentials @api @unhappy", async ({
      request,
    }) => {
      // Pass empty user credentials to verify login API
      const response = await accountApi.verifyLoginApi("", "");

      // Assert 404 response
      expect(response.responseCode).toBe(404);

      // Assert response message
      expect(response.message).toBe("User not found!");
    });

    test("should return 400 when missing email key @api @unhappy", async ({
      request,
    }) => {
      // Pass invalid user credentials to verify login API
      const response = await accountApi.verifyLoginWithPartialData({
        password: "password",
      });

      // Assert 400 response
      expect(response.responseCode).toBe(400);

      // Assert response message
      expect(response.message).toBe(
        "Bad request, email or password parameter is missing in POST request."
      );
    });

    test("should return 400 when missing password key @api @unhappy", async ({
      request,
    }) => {
      // Pass invalid user credentials to verify login API
      const response = await accountApi.verifyLoginWithPartialData({
        email: "test@email.com",
      });

      // Assert 400 response
      expect(response.responseCode).toBe(400);

      // Assert response message
      expect(response.message).toBe(
        "Bad request, email or password parameter is missing in POST request."
      );
    });
  });

  // Complete CRUD flow
  test.describe("Complete API CRUD Flow", () => {
    test("should complete full CRUD cycle: Verify no account > Create Account > Verify account > Delete > Verify no account @api @e2e", async ({
      request,
    }) => {
      // Generate user data
      const user = generateUserData();
      userAccountCredentials = user;

      // 1. Verify Login - Non-existant user (user doesn't have account yet)
      const noUserResponse = await accountApi.verifyLoginApi(
        user.email,
        user.password
      );

      // Assert 404 response
      expect(noUserResponse.responseCode).toBe(404);

      // Assert response message
      expect(noUserResponse.message).toBe("User not found!");

      // 2. Create Account
      const createAccountResponse = await accountApi.createAccountFromUserData(
        user
      );

      // Assert 201 response
      expect(createAccountResponse.responseCode).toBe(201);

      // Assert response message
      expect(createAccountResponse.message).toBe("User created!");

      // 3. Verify Login - Existing user (user has account)
      const existingUserResponse = await accountApi.verifyLoginApi(
        user.email,
        user.password
      );

      // Assert 200 response
      expect(existingUserResponse.responseCode).toBe(200);

      // Assert response message
      expect(existingUserResponse.message).toBe("User exists!");

      // 3. Delete account
      const deleteResponse = await accountApi.deleteAccount(
        user.email,
        user.password
      );

      // Assert 200 response
      expect(deleteResponse.responseCode).toBe(200);

      // Assert response message
      expect(deleteResponse.message).toBe("Account deleted!");

      // 4. Verify Login - Deleted user account
      const deletedAccountResponse = await accountApi.verifyLoginApi(
        user.email,
        user.password
      );

      // Assert 404 response
      expect(deletedAccountResponse.responseCode).toBe(404);

      // Assert response message
      expect(deletedAccountResponse.message).toBe("User not found!");
    });
  });
});
