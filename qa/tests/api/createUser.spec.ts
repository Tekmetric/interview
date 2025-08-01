import { test, expect, request } from '@playwright/test';

//documentation: https://www.automationexercise.com/api_list
test('Create user via API with valid data', async () => {
    const requestContext = await request.newContext();

    const uniqueEmail = `apitestuser_${Date.now()}@example.com`;

    const response = await requestContext.post('https://automationexercise.com/api/createAccount', {
        form: {
            name: 'Test User',
            email: uniqueEmail,
            password: 'StrongPass123!',
            title: 'Mr',
            birth_date: '1',
            birth_month: '1',
            birth_year: '1995',
            firstname: 'Test',
            lastname: 'User',
            company: 'QA company',
            address1: '123 Test St',
            address2: 'Suite 555',
            country: 'United States',
            zipcode: '12345',
            state: 'WI',
            city: 'Middleton',
            mobile_number: '1234567890'
        }
    });

    expect(response.status()).toBe(200);

    const body = await response.json();
    console.log('API response:', body);

    expect(body.responseCode).toBe(201);
    expect(body.message).toContain('User created!');
});
