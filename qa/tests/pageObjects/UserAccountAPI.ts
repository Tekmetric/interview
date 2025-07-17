import { APIRequestContext, APIResponse } from '@playwright/test';

export class UserAccountAPI {
    private request: APIRequestContext;
    private baseUrl: string;
    private authToken: string | null = null;
    private cookieJar: Record<string, string> = {};

    constructor(request: APIRequestContext, baseUrl: string) {
        this.request = request;
        this.baseUrl = baseUrl;
    }

    setAuthToken(token: string) {
        this.authToken = token;
    }

    private getHeaders(): Record<string, string> {
        const headers: Record<string, string> = { 'Content-Type': 'application/x-www-form-urlencoded' };
        if (this.authToken) {
            headers['Authorization'] = `Bearer ${this.authToken}`;
        }
        return headers;
    }

    private getCookies(): string {
        return Object.entries(this.cookieJar).map(([key, value]) => `${key}=${value}`).join('; ');
    }

    private updateHeadersWithCookies(headers: Record<string, string>): Record<string, string> {
        const cookieString = this.getCookies();
        if (cookieString) {
            headers['Cookie'] = cookieString;
        }
        return headers;
    }

    async captureCookies(response: APIResponse) {
        console.log('Response Headers:', response.headers());
        const cookiesHeader = response.headers()['set-cookie'];

        if (cookiesHeader) {
            const cookies = Array.isArray(cookiesHeader) ? cookiesHeader : [cookiesHeader];
            cookies.forEach(cookie => {
                if (cookie) {
                    const parts = cookie.split(';')[0].split('=');
                    if (parts.length === 2) {
                        const [key, value] = parts;
                        this.cookieJar[key] = value;
                    }
                }
            });
            console.log('Captured cookies:', this.cookieJar);
        } else {
            console.log('No Set-Cookie header found');
        }
    }

    async createUser(email: string, password: string, extraParams: Record<string, any>): Promise<APIResponse> {
        try {
            const response = await this.request.post(`${this.baseUrl}/createAccount`, {
                headers: this.updateHeadersWithCookies(this.getHeaders()),
                form: { email, password, ...extraParams },
            });
            await this.captureCookies(response);
            return response;
        } catch (error) {
            console.error('Failed to create user:', error);
            throw error;
        }
    }

    async verifyLogin(email: string, password: string): Promise<APIResponse> {
        try {
            const response = await this.request.post(`${this.baseUrl}/verifyLogin`, {
                headers: this.updateHeadersWithCookies(this.getHeaders()),
                form: { email, password },
            });
            await this.captureCookies(response);
            if (response.ok()) {
                const data = await response.json();
                this.setAuthToken(data.token);
            }
            return response;
        } catch (error) {
            console.error('Login verification failed:', error);
            throw error;
        }
    }

    async updateUser(email: string, password: string, updateParams: Record<string, any>): Promise<APIResponse> {
        try {
            const response = await this.request.put(`${this.baseUrl}/updateAccount`, {
                headers: this.updateHeadersWithCookies(this.getHeaders()),
                form: { email, password, ...updateParams },
            });
            await this.captureCookies(response);
            return response;
        } catch (error) {
            console.error('Failed to update user:', error);
            throw error;
        }
    }

    async deleteUser(email: string, password: string): Promise<APIResponse> {
        try {
            const response = await this.request.delete(`${this.baseUrl}/deleteAccount`, {
                headers: this.updateHeadersWithCookies(this.getHeaders()),
                data: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`,
            });
            await this.captureCookies(response);
            return response;
        } catch (error) {
            console.error('Failed to delete user:', error);
            throw error;
        }
    }
}
