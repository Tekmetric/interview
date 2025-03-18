import { APIRequestContext, expect } from '@playwright/test';

const BASE_URL = 'https://automationexercise.com/api';

export async function searchProduct(request: APIRequestContext, productName: string) {
    const response = await request.post(`${BASE_URL}/searchProduct`, {
        form: {
            search_product: productName
        }
    });

    const responseBody = await response.json();

    expect(response.status()).toBe(200);
    expect(responseBody.responseCode).toBe(200);

    const firstProduct = responseBody.products[0];
    return firstProduct; 
}
