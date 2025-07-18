import { APIRequestContext, expect } from '@playwright/test';

/**
 * API helper class for managing user accounts via REST endpoints.
 * 
 * Provides methods to create, verify, read, update, and delete user accounts.
 * Designed to work with Playwright's APIRequestContext in automated tests.
 *
 * @example
 * const userApi = new UserApi(request, process.env.BASE_URL);
 * const newUser = generateTestUser();
 * await userApi.createUser(newUser);
 */
export class UserApi {
    constructor(
        public readonly request: APIRequestContext,
        public readonly baseUrl: string
    ) { }

    async createUser(payload: {
        name: string;
        email: string;
        title: string;
        password: string;
        birth_day: string;
        birth_month: string;
        birth_year: string;
        firstname: string;
        lastname: string;
        company: string;
        address1: string;
        address2: string;
        country: string;
        zipcode: string;
        state: string;
        city: string;
        mobile_number: string;
    }) {
        const response = await this.request.post(`${this.baseUrl}/api/createAccount`, {
            form: payload,
        });

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(201);
        expect(body.message).toContain('User created!');
        return body;
    }

    async verifyUserExists(email: string, password: string) {
        const response = await this.request.post(`${this.baseUrl}/api/verifyLogin`, {
            form: { email, password },
        });

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(200);
        expect(body.message).toContain('User exists!');
        return body;
    }

    async verifyUserDoesNotExist(email: string, password: string) {
        const response = await this.request.post(`${this.baseUrl}/api/verifyLogin`, {
            form: { email, password },
        });

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(404);
        expect(body.message).toContain('User not found!');
        return body;
    }

    async readUser(email: string) {
        const response = await this.request.get(`${this.baseUrl}/api/getUserDetailByEmail`, {
            params: { email },
        });

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(200);
        return body;
    }

    async updateUser(email: string, password: string, userUpdates: Record<string, string>) {
        const response = await this.request.put(`${this.baseUrl}/api/updateAccount`, {
            form: { email, password, ...userUpdates },
        });

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(200);
        expect(body.message).toContain('User updated!');
        return body;
    }

    async deleteUser(email: string, password: string) {
        const response = await this.request.delete(`${this.baseUrl}/api/deleteAccount`, {
            form: { email, password },
        });

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(200);
        expect(body.message).toContain('Account deleted!');
        return body;
    }
}