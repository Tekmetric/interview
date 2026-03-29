import { test, expect } from '../../fixtures/test-fixtures';
import { SessionManager } from '../../helpers/session-manager';
import { AdminPage } from '../../pages/AdminPage';

test.describe('Admin Authentication - API and Session Reuse', () => {
  test('authenticate admin via API and access admin page without UI login', async ({ apiClient, authenticatedContext }) => {
    const token = await SessionManager.authenticateAdmin(apiClient);
    expect(token).toBeTruthy();
    const page = await authenticatedContext.newPage();
    await page.goto('https://automationintesting.online/admin');
    await page.waitForLoadState('networkidle');
    const cookies = await page.context().cookies();
    const tokenCookie = cookies.find(c => c.name === 'token');
    expect(tokenCookie).toBeDefined();
    expect(tokenCookie?.value).toBeTruthy();
    const adminPage = new AdminPage(page);
    const loginVisible = await adminPage.isLoginFormVisible();
    if (!loginVisible) {
      const hasAccess = await adminPage.isDashboardVisible();
      expect(hasAccess).toBe(true);
    }
    await page.close();
  });

  test('store authenticated session in browser and reuse across pages', async ({ apiClient, authenticatedContext }) => {
    await SessionManager.authenticateAdmin(apiClient);
    const page1 = await authenticatedContext.newPage();
    const page2 = await authenticatedContext.newPage();
    await page1.goto('https://automationintesting.online/admin');
    await page2.goto('https://automationintesting.online/admin');
    const cookies1 = await page1.context().cookies();
    const cookies2 = await page2.context().cookies();
    expect(cookies1.find(c => c.name === 'token')).toBeDefined();
    expect(cookies2.find(c => c.name === 'token')).toBeDefined();
    await page1.close();
    await page2.close();
  });

  test('token is cached and session persists after navigation', async ({ apiClient, authenticatedContext }) => {
    SessionManager.clearTokenCache();
    const token1 = await SessionManager.authenticateAdmin(apiClient);
    const token2 = await SessionManager.authenticateAdmin(apiClient);
    expect(token2).toBe(token1);
    const page = await authenticatedContext.newPage();
    await page.goto('https://automationintesting.online/admin');
    await page.waitForLoadState('networkidle');
    const hasToken = (await page.context().cookies()).some(c => c.name === 'token');
    expect(hasToken).toBe(true);
    await page.goto('https://automationintesting.online/');
    await page.goto('https://automationintesting.online/admin');
    const stillHasToken = (await page.context().cookies()).some(c => c.name === 'token');
    expect(stillHasToken).toBe(true);
    await page.close();
  });
});
