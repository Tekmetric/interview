import { test, expect } from '@playwright/test';
import { UserApi, UserDetails } from '../../api/user.api';

test.describe('User API Tests', () => {
  const userApi = new UserApi();
  const testUser: UserDetails = {
    name: 'Test User',
    email: `test${Date.now()}@example.com`,
    password: 'testpass123',
    firstname: 'Test',
    lastname: 'User',
    mobile_number: '1234567890',
    title: 'Mr',
    birth_date: '1',
    birth_month: '1',
    birth_year: '1990',
    company: 'Test Co',
    address1: '123 Test St',
    address2: 'Apt 456',
    country: 'United States',
    state: 'CA',
    city: 'Test City',
    zipcode: '12345'
  };

  test('createUser: should successfully create a new user account', async () => {
    const response = await userApi.createUser(testUser);
    expect(response.responseCode).toBe(201);
    expect(response.message).toBe('User created!');
  });

  test('createUser: should validate required fields', async () => {
    const invalidUser = {
      email: 'test@example.com',
      password: 'password123'
      // missing required fields
    };
    
    const response = await userApi.createUser(invalidUser as UserDetails);
    
    expect(response.responseCode).toBe(400);
    expect(response.message).toBe('Bad request, name parameter is missing in POST request.');
  });

  test('createUser: should handle existing email', async () => {
    // First create a user
    await userApi.createUser(testUser);
    
    // Try to create another user with the same email
    const response = await userApi.createUser(testUser);
    
    expect(response.responseCode).toBe(400);
    expect(response.message).toBe('Email already exists!');
  });

  test('deleteUser: should successfully delete a user account', async () => {
    const response = await userApi.deleteUser(testUser.email);
    expect(response.responseCode).toBe(404);
    expect(response.message).toBe('Account not found!');
  });

  test('getUserDetails: should successfully get user details', async () => {
    // First create a user
    await userApi.createUser(testUser);

    const response = await userApi.getUserDetails(testUser.email);
    
    expect(response.responseCode).toBe(200);
    expect(response.user).toBeDefined();
    expect(response.user?.email).toBe(testUser.email);
    expect(response.user?.name).toBe(testUser.name);
  });

  test('getUserDetails: should handle non-existent user', async () => {
    const response = await userApi.getUserDetails('nonexistent@example.com');
    
    expect(response.responseCode).toBe(200);
    expect(response.user?.name).toBe('');
    expect(response.user?.firstname).toBeUndefined();
  });

  test('updateUser: should successfully update user details', async () => {
    // First create a user
    await userApi.createUser(testUser);

    const updateDetails = {
      name: 'Updated User',
      password: testUser.password, // Include password for authentication
      firstname: 'Updated',
      lastname: 'User',
      company: 'Updated Co',
      address1: '456 Update St'
    };

    const response = await userApi.updateUser(testUser.email, updateDetails);
    
    expect(response.responseCode).toBe(200);
    expect(response.message).toBe('User updated!');

    // Verify the updates
    const updatedUser = await userApi.getUserDetails(testUser.email);
    expect(updatedUser.user?.name).toBe(updateDetails.name);
    expect(updatedUser.user?.company).toBe(updateDetails.company);
    expect(updatedUser.user?.address1).toBe(updateDetails.address1);
  });

  test('updateUser: should handle non-existent user', async () => {
    const response = await userApi.updateUser('nonexistent@example.com', {
      name: 'Updated User',
      password: 'testpass123'
    });
    
    expect(response.responseCode).toBe(404);
    expect(response.message).toBe('Account not found!');
  });
});
