import { test, expect, request } from '@playwright/test';
import 'dotenv/config';

test('Create Account', async () => {
    // Establish base url
    const context = await request.newContext({
        baseURL: process.env.BASE_API_URL,
    });
    
    // Set fields
    const form = new FormData();
    form.set('name', process.env.Name);
    form.set('email', 'test75@test.com');
    form.set('password', process.env.PASSWORD);
    form.set('title', 'Mrs');
    form.set('birth_date', '4');
    form.set('birth_month', 'May');
    form.set('birth_year', '2001');
    form.set('firstname', 'Alyssa');
    form.set('lastname', 'API');
    form.set('company', 'Test');
    form.set('address1', '1 Main St');
    form.set('address2', '123');
    form.set('country', 'United States');
    form.set('zipcode', '20010');
    form.set('state', 'Massachusetts');
    form.set('city', 'Boston');
    form.set('mobile_number', '5555555555');

    // Create account
    const response = await context.post('createAccount', {
        multipart: form
    });

    // Expect successful response
    const responseBody = await response.json();
    await expect(responseBody.responseCode).toEqual(201);
    await expect(responseBody.message).toContain('User created!');
});

test('Verify valid login credentials', async () => {
    // Establish base url
    const context = await request.newContext({
        baseURL: process.env.BASE_API_URL,
    });

    // Set request parameters
    const form = new FormData();
    form.set('email', process.env.EMAIL);
    form.set('password', process.env.PASSWORD);
  
    // Log in
    const response = await context.post('verifyLogin', {
        multipart: form
    });

    // Expect successful response
    const responseBody = await response.json();
    await expect(responseBody.responseCode).toEqual(200);
    await expect(responseBody.message).toContain('User exists!');
});

test('Verify invalid log in credentials', async () => {
    // Establish base url
    const context = await request.newContext({
        baseURL: process.env.BASE_API_URL,
    });

    // Set request parameters
    const form = new FormData();
    form.set('email', process.env.EMAIL);
    form.set('password', 'test');
  
    // Log in
    const response = await context.post('verifyLogin', {
        multipart: form
    });

    // Expect error response
    const responseBody = await response.json();
    await expect(responseBody.responseCode).toEqual(404);
    await expect(responseBody.message).toContain('User not found!');
});

test('Get user details', async () => {
    // Establish base url
    const context = await request.newContext({
        baseURL: process.env.BASE_API_URL,
    });

    // Set request parameters
    const searchParams = new URLSearchParams();
    searchParams.set('email', 'test75@test.com');

    // Get user details
    const response = await context.get('getUserDetailByEmail', { params: searchParams });

    // Expect successful response
    const responseBody = await response.json();
    await expect(responseBody.responseCode).toEqual(200);
    await expect(responseBody.user.email).toBe('test75@test.com');
});

test('Update user details', async () => {
    // Establish base url
    const context = await request.newContext({
        baseURL: process.env.BASE_API_URL,
    });

    // Randomize values to update
    const mobileNumberUpdate = Math.random().toString().slice(2, 12);
    const birthDateUpdate = Math.floor(Math.random() * 30) + 1 + '';

    const form = new FormData();
    form.set('email', 'test75@test.com');
    form.set('password', process.env.PASSWORD);
    form.set('birth_date', birthDateUpdate);
    form.set('mobile_number', mobileNumberUpdate);

    // Update user details
    const response = await context.put('updateAccount', {
        multipart: form,
    });

    // Expect successful response
    const responseBody = await response.json();
    await expect(responseBody.responseCode).toEqual(200);
    await expect(responseBody.message).toContain('User updated!');
});

test('Delete user', async () => {
    // Establish base url
    const context = await request.newContext({
        baseURL: process.env.BASE_API_URL,
    });

    // Set request parameters
    const form = new FormData();
    form.set('email', 'test75@test.com');
    form.set('password', process.env.PASSWORD);
  
    // Delete account
    const response = await context.delete('deleteAccount', {
        multipart: form
    });

    // Expect successful response
    const responseBody = await response.json();
    await expect(responseBody.responseCode).toEqual(200);
    await expect(responseBody.message).toContain('Account deleted!');
});