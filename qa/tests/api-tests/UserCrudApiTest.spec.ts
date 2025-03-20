import { test, expect, request } from '@playwright/test';

const BASE_URL = 'https://automationexercise.com/api';

let userEmail = `testuser${Date.now()}@example.com`;

test.describe.configure({ mode: 'serial' });

test('', async () => {
    // Create a new API request context using `request.newContext()`
    const apiRequest = await request.newContext();

    // Send login request using form-data
    const loginResponse = await apiRequest.post(`${BASE_URL}/verifyLogin`, {
        form: {
            email: 'tashmanalan@gmail.com',
            password: 'Tashman1234'
        }
    });

    expect(loginResponse.status()).toBe(200);

    const responseData = await loginResponse.json();
    expect(responseData.message).toBe('User exists!')
});

// **1. CREATE a New User**
test.beforeAll(async ({ request }) => {
    const response = await request.post(`${BASE_URL}/createAccount`, {
        form: {
            name: 'NewTestUser',
            email: userEmail,
            password: 'Test@123',
            title: 'Mr',
            birth_day: '1',
            birth_month: '1',
            birth_year: '1990',
            firstname: 'John',
            lastname: 'Doe',
            company: 'Abc123',
            address1: '123 Washington St',
            address2: '125 Dampster St',
            country: 'United States',
            city: 'Chicago',
            state: 'IL',
            zipcode: 60606,
            mobile_number: '8721234566'
        }
    });

    expect(response.status()).toBe(200);
    const responseData = await response.json();
    const responseMessage = responseData.message;
    expect(responseMessage).toBe('User created!');
});


// 2. UPDATE User Details
test('Update User API', async ({ request }) => {
    const response = await request.put(`${BASE_URL}/updateAccount`, {
        form: {
            name: 'UpdatedTestUserName',
            email: userEmail,
            password: 'Test@123',
            title: 'Mr',
            birth_day: '1',
            birth_month: '1',
            birth_year: '1990',
            firstname: 'John',
            lastname: 'Doe',
            company: 'Abc123',
            address1: '123 Washington St',
            address2: '125 Dampster St',
            country: 'United States',
            city: 'Chicago',
            state: 'IL',
            zipcode: 60606,
            mobile_number: '8721234566'
        }
    });

    // Validate response status
    expect(response.status()).toBe(200);

    // Parse JSON response
    const responseData = await response.json();
    expect(responseData.message).toBe('User updated!');
});

// 3. Get newly created user
test('Get user details', async ({ request }) => {
    const response = await request.get(`${BASE_URL}/getUserDetailByEmail`, {
        params: {
            email: userEmail
        }
    });

    expect(response.status()).toBe(200); // No content response
    const responseBody = await response.json();
    expect(responseBody.user.email).toBe(userEmail);
    expect(responseBody.user.name).toBe('UpdatedTestUserName');
});

// 4. DELETE User using email and password
test('Delete User API', async ({ request }) => {
    const response = await request.delete(`${BASE_URL}/deleteAccount`, {
        form: {
            email: userEmail,
            password: 'Test@123'
        }
    });

    expect(response.status()).toBe(200); // No content response
    const responseBody = await response.json();
    expect(responseBody.message).toBe('Account deleted!');
});
