import { ApiError, ApiUser, TableData } from '../types';
import { UnknownError } from '../types';

// Constants
const API_CONFIG = {
  BASE_URL: import.meta.env.VITE_API_URL || 'https://68a8ab89b115e67576e98576.mockapi.io/api/v1',
  ENDPOINTS: {
    USERS: '/users',
  },
} as const;

// Debug: Log the API URL being used (only in development)
if (import.meta.env.DEV) {
  console.log('API Base URL:', API_CONFIG.BASE_URL);
  console.log('Environment:', import.meta.env.MODE);
}

const HTTP_STATUS = {
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  UNPROCESSABLE_ENTITY: 422,
  TOO_MANY_REQUESTS: 429,
  INTERNAL_SERVER_ERROR: 500,
  BAD_GATEWAY: 502,
  SERVICE_UNAVAILABLE: 503,
  GATEWAY_TIMEOUT: 504,
} as const;

/**
 * API Service for managing external data operations
 * Handles CRUD operations with proper error handling and type safety
 */
export class ApiService {
  private static readonly BASE_URL = API_CONFIG.BASE_URL;

  /**
   * Creates a structured API error with human-readable messages
   * @param originalError The original error object
   * @returns A formatted ApiError object
   */
  private static createApiError(originalError: UnknownError): ApiError {
    let message = 'An unexpected error occurred';

    if (originalError instanceof Error) {
      if (originalError.message.includes('HTTP error')) {
        const statusMatch = originalError.message.match(/status: (\d+)/);
        const status = statusMatch ? parseInt(statusMatch[1]) : 0;

        switch (status) {
          case HTTP_STATUS.BAD_REQUEST:
            message =
              'Invalid data provided. Please check that all required fields are filled correctly and try again.';
            break;
          case HTTP_STATUS.UNAUTHORIZED:
            message = 'Authentication required. You need to be logged in to perform this action.';
            break;
          case HTTP_STATUS.FORBIDDEN:
            message = "Permission denied. You don't have permission to perform this action.";
            break;
          case HTTP_STATUS.NOT_FOUND:
            message = "User not found. The user you're trying to modify no longer exists.";
            break;
          case HTTP_STATUS.UNPROCESSABLE_ENTITY:
            message =
              'Data validation failed. Some of the information provided is invalid or incomplete.';
            break;
          case HTTP_STATUS.TOO_MANY_REQUESTS:
            message = 'Too many requests. Please wait a moment before trying again.';
            break;
          case HTTP_STATUS.INTERNAL_SERVER_ERROR:
            message = 'Server error. Our servers are experiencing issues. Please try again later.';
            break;
          case HTTP_STATUS.BAD_GATEWAY:
          case HTTP_STATUS.SERVICE_UNAVAILABLE:
          case HTTP_STATUS.GATEWAY_TIMEOUT:
            message =
              'Service temporarily unavailable. Our servers are temporarily down for maintenance. Please try again in a few minutes.';
            break;
          default:
            message = `Server responded with error ${status}. An unexpected server error occurred. Please try again.`;
        }
      } else if (
        originalError.message.includes('Failed to fetch') ||
        originalError.message.includes('NetworkError')
      ) {
        message = 'Network connection failed. Please check your internet connection and try again.';
      } else {
        message = `${originalError.message}. If this problem persists, please contact support.`;
      }
    }

    return {
      message,
      timestamp: new Date(),
    };
  }

  /**
   * Fetches users from the Mockapi.io API
   * @returns Promise that resolves to an array of TableData
   * @throws Error if the API request fails
   */
  static async fetchUsers(): Promise<TableData[]> {
    try {
      const response = await fetch(`${ApiService.BASE_URL}${API_CONFIG.ENDPOINTS.USERS}`);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const users = await response.json();

      // Mockapi.io returns data in our expected format, minimal transformation needed
      return users.map(
        (user: ApiUser): TableData => ({
          id: user.id,
          name: user.name,
          email: user.email,
          status: user.status || 'Active',
          createdAt: user.createdAt || new Date().toISOString(),
          phone: user.phone,
          company: user.company || 'No Company',
        })
      );
    } catch (error) {
      throw ApiService.createApiError(error);
    }
  }

  /**
   * Fetches a single user by ID
   * @param id User ID to fetch
   * @returns Promise that resolves to the user data
   * @throws Error if user not found or fetch fails
   */
  static async getUser(id: string): Promise<TableData> {
    try {
      const response = await fetch(`${ApiService.BASE_URL}${API_CONFIG.ENDPOINTS.USERS}/${id}`);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const user: ApiUser = await response.json();

      return {
        id: user.id,
        name: user.name,
        email: user.email,
        status: user.status || 'Active',
        createdAt: user.createdAt || new Date().toISOString(),
        phone: user.phone,
        company: user.company || 'No Company',
      };
    } catch (error) {
      throw ApiService.createApiError(error);
    }
  }

  /**
   * Creates a new user via the API
   * @param userData User data without id and createdAt
   * @returns Promise that resolves to the created user
   * @throws ApiError if the operation fails
   */
  static async createUser(userData: Omit<TableData, 'id' | 'createdAt'>): Promise<TableData> {
    try {
      const response = await fetch(`${ApiService.BASE_URL}${API_CONFIG.ENDPOINTS.USERS}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: userData.name,
          email: userData.email,
          phone: userData.phone,
          company: userData.company,
          status: userData.status,
          createdAt: new Date().toISOString(),
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const newUser = await response.json();

      // Transform response to match our TableData interface
      return {
        id: newUser.id,
        name: newUser.name,
        email: newUser.email,
        status: newUser.status || userData.status,
        createdAt: newUser.createdAt || new Date().toISOString(),
        phone: newUser.phone,
        company: newUser.company || userData.company,
      };
    } catch (error) {
      throw ApiService.createApiError(error);
    }
  }

  /**
   * Updates an existing user via the API
   * @param id User ID to update
   * @param userData Partial user data for update
   * @returns Promise that resolves to the updated user
   * @throws ApiError if the operation fails
   */
  static async updateUser(id: string, userData: Partial<TableData>): Promise<TableData> {
    try {
      const response = await fetch(`${ApiService.BASE_URL}${API_CONFIG.ENDPOINTS.USERS}/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: userData.name,
          email: userData.email,
          phone: userData.phone,
          company: userData.company,
          status: userData.status,
          createdAt: userData.createdAt,
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const updatedUser = await response.json();

      // Transform response to match our TableData interface
      return {
        id: updatedUser.id,
        name: updatedUser.name,
        email: updatedUser.email,
        status: updatedUser.status || userData.status || 'Active',
        createdAt: updatedUser.createdAt || userData.createdAt || new Date().toISOString(),
        phone: updatedUser.phone,
        company: updatedUser.company || userData.company || '',
      };
    } catch (error) {
      throw ApiService.createApiError(error);
    }
  }

  /**
   * Deletes a user via the API
   * @param id User ID to delete
   * @returns Promise that resolves to true if successful
   * @throws ApiError if the operation fails
   */
  static async deleteUser(id: string): Promise<boolean> {
    try {
      const response = await fetch(`${ApiService.BASE_URL}${API_CONFIG.ENDPOINTS.USERS}/${id}`, {
        method: 'DELETE',
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Mockapi.io returns the deleted object or empty response for successful deletion
      return true;
    } catch (error) {
      throw ApiService.createApiError(error);
    }
  }

  // Simulate API delay for realistic experience
  static async delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}

export default ApiService;
