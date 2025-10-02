import axios from 'axios';

const BASE_URL = 'https://www.automationexercise.com/api';

interface LoginResponse {
  responseCode: number;
  message: string;
}

export class LoginApi {
  /**
   * API 7: Verify Login with valid details
   * @param email User's email
   * @param password User's password
   * @returns Promise<LoginResponse>
   */
  async verifyLogin(email: string, password: string): Promise<LoginResponse> {
    try {
      const params = new URLSearchParams();
      params.append('email', email);
      params.append('password', password);
      const response = await axios.post(`${BASE_URL}/verifyLogin`, params);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        return error.response.data;
      }
      throw error;
    }
  }

  /**
   * API 8: Verify Login without email parameter
   * Tests error handling when email is missing
   * @param password User's password
   * @returns Promise<LoginResponse>
   */
  async verifyLoginWithoutEmail(password: string): Promise<LoginResponse> {
    try {
      const response = await axios.post(`${BASE_URL}/verifyLogin`, {
        password
      });
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        return error.response.data;
      }
      throw error;
    }
  }

  /**
   * API 9: DELETE To Verify Login
   * Tests error handling when using incorrect HTTP method
   * @param email User's email
   * @param password User's password
   * @returns Promise<LoginResponse>
   */
  async deleteVerifyLogin(email: string, password: string): Promise<LoginResponse> {
    try {
      const response = await axios.delete(`${BASE_URL}/verifyLogin`, {
        data: {
          email,
          password
        }
      });
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        return error.response.data;
      }
      throw error;
    }
  }

  /**
   * API 10: Verify Login with invalid details
   * Tests login with invalid credentials
   * @param email Invalid user email
   * @param password Invalid password
   * @returns Promise<LoginResponse>
   */
  async verifyLoginWithInvalidDetails(email: string, password: string): Promise<LoginResponse> {
    try {
      const params = new URLSearchParams();
      params.append('email', email);
      params.append('password', password);
      const response = await axios.post(`${BASE_URL}/verifyLogin`, params);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        return error.response.data;
      }
      throw error;
    }
  }
}
