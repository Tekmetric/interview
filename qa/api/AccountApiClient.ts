import { APIRequestContext } from "@playwright/test";
import { UserData } from "../utils/test-data-generator";

/**
 * AccountApiClient - Handles account-related API operations
 * Base URL: https://automationexercise.com/api
 */
export class AccountApiClient {
  private request: APIRequestContext;
  private baseURL = "https://automationexercise.com/api";

  constructor(request: APIRequestContext) {
    this.request = request;
  }

  /**
   * Delete a user account via API
   * @param email - User's email address
   * @param password - User's password
   * @returns API response with success/error message
   */
  async deleteAccount(
    email: string,
    password: string
  ): Promise<{
    responseCode: number;
    message: string;
  }> {
    try {
      const response = await this.request.delete(
        `${this.baseURL}/deleteAccount`,
        {
          form: {
            email,
            password,
          },
        }
      );

      const responseBody = await response.json();

      return {
        responseCode: responseBody.responseCode || response.status(),
        message: responseBody.message || "",
      };
    } catch (error) {
      console.error("Error deleting account:", error);
      throw error;
    }
  }

  /**
   * Verify account deletion was successful
   * @param email - User's email
   * @param password - User's password
   */
  async verifyAccountDeleted(
    email: string,
    password: string
  ): Promise<boolean> {
    const result = await this.deleteAccount(email, password);
    return result.responseCode === 200 && result.message === "Account deleted!";
  }

  /**
   * Safe delete - doesn't throw error if account doesn't exist
   * Useful for cleanup in afterEach
   */
  async safeDeleteAccount(email: string, password: string): Promise<void> {
    try {
      const result = await this.deleteAccount(email, password);
      // If response indicates failure, just log it
      if (result.responseCode !== 200) {
        console.log(`Account ${email} not deleted: ${result.message}`);
      }
    } catch (error) {
      // Silently ignore errors during cleanup
      console.log(`Could not delete account ${email}:`, error);
    }
  }

  /**
   * Create a new user account via API
   * @param accountData - User account data
   * @returns API response with success/error message
   */
  async createAccount(accountData: {
    name: string;
    email: string;
    password: string;
    title?: string; // Mr, Mrs
    birth_date?: string;
    birth_month?: string;
    birth_year?: string;
    firstname: string;
    lastname: string;
    company?: string;
    address1: string;
    address2?: string;
    country: string;
    zipcode: string;
    state: string;
    city: string;
    mobile_number: string;
  }): Promise<{
    responseCode: number;
    message: string;
  }> {
    try {
      const response = await this.request.post(
        `${this.baseURL}/createAccount`,
        {
          form: accountData,
        }
      );

      const responseBody = await response.json();

      return {
        responseCode: responseBody.responseCode || response.status(),
        message: responseBody.message || "",
      };
    } catch (error) {
      console.error("Error creating account:", error);
      throw error;
    }
  }

  /**
   * Create account from UserData object (convenience method)
   * Handles field name mapping automatically
   */
  async createAccountFromUserData(userData: UserData): Promise<{
    responseCode: number;
    message: string;
  }> {
    return this.createAccount({
      name: userData.fullName,
      email: userData.email,
      password: userData.password,
      title: userData.title,
      birth_date: userData.dateOfBirth?.day,
      birth_month: userData.dateOfBirth?.month,
      birth_year: userData.dateOfBirth?.year,
      firstname: userData.firstName,
      lastname: userData.lastName,
      company: userData.company,
      address1: userData.address,
      address2: userData.address2,
      country: userData.country,
      zipcode: userData.zipcode,
      state: userData.state,
      city: userData.city,
      mobile_number: userData.mobileNumber,
    });
  }

  /**
   * Verify user login with valid credentials
   * @param email - User's email address
   * @param password - User's password
   * @returns API response with success/error message
   */
  async verifyLoginApi(
    email: string,
    password: string
  ): Promise<{
    responseCode: number;
    message: string;
  }> {
    try {
      const response = await this.request.post(`${this.baseURL}/verifyLogin`, {
        form: {
          email,
          password,
        },
      });

      const responseBody = await response.json();

      return {
        responseCode: responseBody.responseCode || response.status(),
        message: responseBody.message || "",
      };
    } catch (error) {
      console.error("Error verifying login:", error);
      throw error;
    }
  }

  async verifyLoginWithPartialData(data: {
    email?: string;
    password?: string;
  }): Promise<{
    responseCode: number;
    message: string;
  }> {
    try {
      // Build form data, only including defined values
      const formData: Record<string, string> = {};
      if (data.email !== undefined) formData.email = data.email;
      if (data.password !== undefined) formData.password = data.password;

      const response = await this.request.post(`${this.baseURL}/verifyLogin`, {
        form: formData,
      });

      const responseBody = await response.json();
      return {
        responseCode: responseBody.responseCode || response.status(),
        message: responseBody.message || "",
      };
    } catch (error) {
      console.error("Error verifying login with partial data:", error);
      throw error;
    }
  }
}
