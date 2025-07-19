import { test, expect, APIRequestContext } from '@playwright/test';
import * as userData from '../../testData/userData.json';
import { generateUniqueEmail } from '../../utils/generateUniqueEmail';
import { UserAccountAPI } from '../pageObjects/UserAccountAPI';
import { getApiUrl } from '../../utils/envHelpers';
import dotenv from 'dotenv';
import path from 'path';

// Load environment variables
dotenv.config({ path: path.resolve(__dirname, '../../.env') });

const apiUrl = getApiUrl();

test.describe.serial('User Account API Tests', () => {
  let api: UserAccountAPI;
  let apiRequestContext: APIRequestContext;
  let email: string;
  const password: string = userData.password;

  test.beforeAll(async ({ playwright }) => {
    email = generateUniqueEmail();
    apiRequestContext = await playwright.request.newContext();
    api = new UserAccountAPI(apiRequestContext, apiUrl);
  });

  test.afterAll(async () => {
    await apiRequestContext.dispose();
  });

  test('Create/Register User Account', async () => {
    const response = await api.createUser(email, password, userData);
    const responseData = await response.json();

    expect(response.status()).toBe(200);
    expect(responseData.responseCode).toBe(201);
    expect(responseData.message).toBe('User created!');
  });

  test('Verify Login', async () => {
    const response = await api.verifyLogin(email, password);
    const responseData = await response.json();

    expect(response.status()).toBe(200);
    expect(responseData.message).toBe('User exists!');
  });

  test('Update User Account', async () => {
    const updatedData = { firstname: 'Updated', lastname: 'User' };
    const response = await api.updateUser(email, password, updatedData);
    const responseData = await response.json();

    expect(response.status()).toBe(200);
    expect(responseData.message).toBe('User updated!');
  });

  test('Delete User Account', async () => {
    const response = await api.deleteUser(email, password);
    const responseData = await response.json();

    expect(response.status()).toBe(200);
    expect(responseData.message).toBe('Account deleted!');
  });
});
