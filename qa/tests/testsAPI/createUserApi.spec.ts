import * as userData from '../../testData/userData.json'; // Import user data
import { generateUniqueEmail } from '../../utils/generateUniqueEmail'; // Import email generator
import { test, expect, APIRequestContext, request } from '@playwright/test';
import { UserAccountAPI } from '../pageObjects/UserAccountAPI';


let api: UserAccountAPI;
let email: string;
const password: string = userData.password;
const baseUrl = 'https://automationexercise.com/api';

test.describe.serial('User Account API Tests', () => {
  let apiRequestContext: APIRequestContext;

  test.beforeAll(async ({ playwright }) => {
    email = generateUniqueEmail();
    apiRequestContext = await playwright.request.newContext();
    api = new UserAccountAPI(apiRequestContext, baseUrl);
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
