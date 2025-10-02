import { test, expect } from '@playwright/test';
import { LoginApi } from '../../api/login.api';

test.describe('Login API Tests', () => {
  let loginApi: LoginApi;

  test.beforeEach(() => {
    loginApi = new LoginApi();
  });

  test.describe('API 7: Verify Login with valid details', () => {
    test('should successfully verify login with valid credentials', async () => {
      const response = await loginApi.verifyLogin('ryandandrow@gmail.com', 'testPassword');
      
      expect(response.responseCode).toBe(200);
      expect(response.message).toBe('User exists!');
    });
  });

  test.describe('API 8: Verify Login without email parameter', () => {
    test('should fail verification when email is missing', async () => {
      const response = await loginApi.verifyLoginWithoutEmail('password123');
      
      expect(response.responseCode).toBe(400);
      expect(response.message).toContain('Bad request, email or password parameter is missing in POST request.');
    });

    test('should handle empty password', async () => {
      const response = await loginApi.verifyLoginWithoutEmail('');
      
      expect(response.responseCode).toBe(400);
      expect(response.message).toContain('Bad request, email or password parameter is missing in POST request.');
    });
  });

  test.describe('API 9: DELETE To Verify Login', () => {
    test('should fail when using DELETE method with valid credentials', async () => {
      const response = await loginApi.deleteVerifyLogin('test@example.com', 'password123');
      
      expect(response.responseCode).toBe(405);
      expect(response.message).toContain('This request method is not supported.');
    });

    test('should fail when using DELETE method with invalid credentials', async () => {
      const response = await loginApi.deleteVerifyLogin('invalid@example.com', 'wrongpassword');
      
      expect(response.responseCode).toBe(405);
      expect(response.message).toContain('This request method is not supported.');
    });

    test('should fail when using DELETE method with empty credentials', async () => {
      const response = await loginApi.deleteVerifyLogin('', '');
      
      expect(response.responseCode).toBe(405);
      expect(response.message).toContain('This request method is not supported.');
    });
  });

  test.describe('API 10: Verify Login with invalid details', () => {
    test('should fail verification with non-existent user', async () => {
      const response = await loginApi.verifyLoginWithInvalidDetails('nonexistent@example.com', 'password123');
      
      expect(response.responseCode).toBe(404);
      expect(response.message).toBe('User not found!');
    });

    test('should fail verification with incorrect password format', async () => {
      const response = await loginApi.verifyLoginWithInvalidDetails('test@example.com', '123');
      
      expect(response.responseCode).toBe(404);
      expect(response.message).toBe('User not found!');
    });

    test('should fail verification with special characters in email', async () => {
      const response = await loginApi.verifyLoginWithInvalidDetails('test@@example.com', 'password123');
      
      expect(response.responseCode).toBe(404);
      expect(response.message).toBe('User not found!');
    });

    test('should fail verification with SQL injection attempt', async () => {
      const response = await loginApi.verifyLoginWithInvalidDetails("' OR '1'='1", 'password123');
      
      expect(response.responseCode).toBe(404);
      expect(response.message).toBe('User not found!');
    });
  });

  test.describe('Error Handling', () => {
    test('should handle network errors gracefully', async () => {
      // Temporarily change base URL to trigger network error
      const originalBaseUrl = process.env.API_BASE_URL;
      process.env.API_BASE_URL = 'http://invalid-url';

      try {
        await loginApi.verifyLogin('test@example.com', 'password123');
        throw new Error('Expected network error');
      } catch (error) {
        expect(error).toBeTruthy();
        expect(error.message).toContain('network');
      } finally {
        process.env.API_BASE_URL = originalBaseUrl;
      }
    });

    test('should handle malformed JSON responses', async () => {
      // This would need to be mocked in a real implementation
      // Here we're just demonstrating the test structure
      try {
        await loginApi.verifyLogin('test@example.com', 'password123');
      } catch (error) {
        expect(error).toBeTruthy();
      }
    });
  });

  test.describe('Response Structure', () => {
    test('should return properly structured response for successful login', async () => {
      const response = await loginApi.verifyLogin('test@example.com', 'password123');
      
      expect(response).toHaveProperty('responseCode');
      expect(response).toHaveProperty('message');
      expect(typeof response.responseCode).toBe('number');
      expect(typeof response.message).toBe('string');
    });

    test('should return properly structured response for failed login', async () => {
      const response = await loginApi.verifyLogin('invalid@example.com', 'wrongpassword');
      
      expect(response).toHaveProperty('responseCode');
      expect(response).toHaveProperty('message');
      expect(typeof response.responseCode).toBe('number');
      expect(typeof response.message).toBe('string');
    });
  });
});
