import { APIRequestContext, BrowserContext } from "@playwright/test";

const ADMIN_USERNAME = process.env.ADMIN_USERNAME;
const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD;

const RESTFUL_BOOKER_USERNAME = process.env.RESTFUL_BOOKER_USERNAME;
const RESTFUL_BOOKER_PASSWORD = process.env.RESTFUL_BOOKER_PASSWORD;

/**
 * Creates a new auth token to use for access to PUT and DELETE for restful-booker API
 * @param request
 * @returns returns token
 */
export async function getApiToken(request: APIRequestContext): Promise<string> {
  const res = await request.post(process.env.API_BASE_URL + "/auth", {
    data: {
      username: RESTFUL_BOOKER_USERNAME,
      password: RESTFUL_BOOKER_PASSWORD,
    },
  });

  const body = await res.json();
  return body.token;
}

/**
 * Authenticating and injecting the cookie
 * @param request
 * @param context
 */
export async function injectAdminSessionCookie(
  request: APIRequestContext,
  context: BrowserContext,
): Promise<void> {
  const res = await request.post(process.env.BASE_URL + "/api/auth/login", {
    data: {
      username: ADMIN_USERNAME,
      password: ADMIN_PASSWORD,
    },
  });

  const body = await res.json();
  const token = body.token;
  const domain = new URL(process.env.BASE_URL!).hostname;

  await context.addCookies([
    {
      name: "token",
      value: token,
      domain: domain,
      path: "/",
    },
  ]);
}
