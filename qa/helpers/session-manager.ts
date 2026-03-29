import { BrowserContext, Page } from '@playwright/test';
import { ApiClient } from '../api/api-client';
import { BASE_URL } from '../utils/constants';

export class SessionManager {
  private static tokenCache: string | null = null;
  private static tokenTimestamp: number = 0;
  private static readonly TOKEN_CACHE_TTL = 10 * 60 * 1000;

  static async authenticateAdmin(apiClient: ApiClient): Promise<string> {
    const now = Date.now();
    
  
    if (this.tokenCache && (now - this.tokenTimestamp) < this.TOKEN_CACHE_TTL) {
      return this.tokenCache;
    }

  
    const token = await apiClient.authenticateAdmin();
    this.tokenCache = token;
    this.tokenTimestamp = now;
    
    return token;
  }


  static async setAuthenticatedSession(context: BrowserContext, token: string): Promise<void> {
    await context.addCookies([
      {
        name: 'token',
        value: token,
        domain: 'automationintesting.online',
        path: '/',
        httpOnly: true,
        secure: true,
        sameSite: 'Lax',
      },
    ]);
  }

 
  static async setupAuthenticatedContext(
    context: BrowserContext,
    apiClient: ApiClient
  ): Promise<void> {
    const token = await this.authenticateAdmin(apiClient);
    await this.setAuthenticatedSession(context, token);
  }

 
  static clearTokenCache(): void {
    this.tokenCache = null;
    this.tokenTimestamp = 0;
  }

 
  static getCachedToken(): string | null {
    return this.tokenCache;
  }

  static async navigateToAdminPage(page: Page): Promise<void> {
    await page.goto(`${BASE_URL}/admin`);
   
    await page.waitForLoadState('networkidle');
  }
}
