import { test, expect } from '@playwright/test';
import { RegisterHelper } from '../lib/register_helper';

// Creates an account, Gets account details, Updates account details, then Deletes the account via API
test('User Account CRUD API', async ({ page }) => {
    const register_helper = new RegisterHelper();

    // Create an account via API. Assert success.
    const create_response = await register_helper.api_create_account();
    expect(create_response.status).toBe(200);
    const create_response_json = await create_response.json();
    expect(create_response_json).toHaveProperty('responseCode', 201);
    expect(create_response_json).toHaveProperty('message', 'User created!');

    // Get account details via API. Assert success and correct details.
    const get_response = await register_helper.api_get_account_details(register_helper.email);
    expect(get_response.status).toBe(200);
    const get_response_json = await get_response.json();
    expect(get_response_json).toHaveProperty('responseCode', 200);
    expect(get_response_json).toHaveProperty('user.name', register_helper.name);
    expect(get_response_json).toHaveProperty('user.email', register_helper.email);

    // Update account details via API. Assert success.
    const new_name = register_helper.name + '_updated';
    const new_address = register_helper.address + ' Apt 2';
    const update_response = await register_helper.api_update_account_details(
        new_name,
        register_helper.email,
        register_helper.password,
        '', // Title
        '', // Birthday
        '', // Birthmonth
        '', // Birthyear
        '', // Firstname
        '', // Lastname
        '', // Company
        new_address
    );
    expect(update_response.status).toBe(200);
    const update_response_json = await update_response.json();
    expect(update_response_json).toHaveProperty('responseCode', 200);
    expect(update_response_json).toHaveProperty('message', 'User updated!');

    // Get account details again via API. Assert success and updated details.
    const get_updated_response = await register_helper.api_get_account_details(register_helper.email);
    expect(get_updated_response.status).toBe(200);
    const get_updated_response_json = await get_updated_response.json();
    expect(get_updated_response_json).toHaveProperty('responseCode', 200);
    expect(get_updated_response_json).toHaveProperty('user.name', new_name);
    expect(get_updated_response_json).toHaveProperty('user.address1', new_address);

    // Delete account via API. Assert success.
    const delete_response = await register_helper.api_delete_account(register_helper.email, register_helper.password);
    expect(delete_response.status).toBe(200);
    const delete_response_json = await delete_response.json();
    expect(delete_response_json).toHaveProperty('responseCode', 200);
    expect(delete_response_json).toHaveProperty('message', 'Account deleted!');

    // Verify account deletion by attempting to get account details again.
    const get_deleted_response = await register_helper.api_get_account_details(register_helper.email);
    expect(get_deleted_response.status).toBe(200);
    const get_deleted_response_json = await get_deleted_response.json();
    expect(get_deleted_response_json).toHaveProperty('responseCode', 404);
    expect(get_deleted_response_json).toHaveProperty('message', 'Account not found with this email, try another email!');
});