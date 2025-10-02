import axios from 'axios';

const BASE_URL = 'https://automationexercise.com/api';

export interface UserResponse {
  responseCode: number;
  message: string;
}

export interface UserDetails {
  name: string;
  email: string;
  password: string;
  title?: string;
  birth_date?: string;
  birth_month?: string;
  birth_year?: string;
  firstname?: string;
  lastname?: string;
  company?: string;
  address1?: string;
  address2?: string;
  country?: string;
  state?: string;
  city?: string;
  zipcode?: string;
  mobile_number?: string;
}

interface UserAccountResponse extends UserResponse {
  user?: UserDetails;
}

export class UserApi {
  /**
   * API 11: Create/Register User Account
   * @param userDetails User registration details
   * @returns Promise<UserResponse>
   */
  async createUser(userDetails: UserDetails): Promise<UserResponse> {
    try {
      const formData = new URLSearchParams();
      // Add all fields from the userDetails
      Object.entries(userDetails).forEach(([key, value]) => {
        if (value !== undefined) {
          formData.append(key, value.toString());
        }
      });
      const response = await axios.post(`${BASE_URL}/createAccount`, formData, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
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
   * API 12: Delete User Account
   * @param email User's email
   * @returns Promise<UserResponse>
   */
  async deleteUser(email: string): Promise<UserResponse> {
    try {
      const formData = new URLSearchParams();
      formData.append('email', email);
      formData.append('password', 'testpass123');
      
      const response = await axios.delete(`${BASE_URL}/deleteAccount`, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        data: formData
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
   * API 13: Update User Account
   * @param email User's email to update
   * @param updateDetails Updated user details
   * @returns Promise<UserResponse>
   */
  async updateUser(email: string, updateDetails: Partial<UserDetails>): Promise<UserResponse> {
    try {
      const formData = new URLSearchParams();
      formData.append('email', email);
      Object.entries(updateDetails).forEach(([key, value]) => {
        formData.append(key, value);
      });
      const response = await axios.put(`${BASE_URL}/updateAccount`, formData, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
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
   * API 14: Get User Account Detail by Email
   * @param email User's email
   * @returns Promise<UserAccountResponse>
   */
  async getUserDetails(email: string): Promise<UserAccountResponse> {
    try {
      const response = await axios.get(`${BASE_URL}/getUserDetailByEmail`, {
        params: { email }
      });
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        return error.response.data;
      }
      throw error;
    }
  }
}
