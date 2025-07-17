import { APIRequestContext, APIRequest } from '@playwright/test';

export class UserAccountAPI {
    private request: APIRequestContext;
    private baseUrl: string;

    constructor(request: APIRequestContext, baseUrl: string) {
        this.request = request;
        this.baseUrl = baseUrl;
    }

    async createUser(email: string, password: string, extraParams: Record<string, any>) {
        const response = await this.request.post(`${this.baseUrl}/createAccount`, {
            form: { email, password, ...extraParams },
        });
        return response;
    }

    async verifyLogin(email: string, password: string) {
        const response = await this.request.post(`${this.baseUrl}/verifyLogin`, {
            form: { email, password },
        });
        return response;
    }

    async updateUser(email: string, password: string, updateParams: Record<string, any>) {
        const response = await this.request.put(`${this.baseUrl}/updateAccount`, {
            form: { email, password, ...updateParams },
        });
        return response;
    }

    async deleteUser(email: string, password: string) {
        const response = await this.request.delete(`${this.baseUrl}/deleteAccount`, {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            data: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`,
        });
        return response;
    }
}
