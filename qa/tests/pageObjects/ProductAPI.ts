import { APIRequestContext, APIResponse } from '@playwright/test';

export class ProductAPI {
  private request: APIRequestContext;
  private baseUrl: string;

  constructor(request: APIRequestContext, baseUrl: string) {
    this.request = request;
    this.baseUrl = baseUrl;
  }

  async searchProduct(searchTerm: string): Promise<APIResponse> {
    const response = await this.request.post(`${this.baseUrl}/searchProduct`, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      data: new URLSearchParams({ search_product: searchTerm }).toString(), // Encode data for x-www-form-urlencoded
    });

    if (!response.ok()) {
      console.error(`Failed to search product: ${response.status()} - ${response.statusText()}`);
      throw new Error(`Error while searching for products: ${response.statusText()}`);
    }

    return response;
  }
}