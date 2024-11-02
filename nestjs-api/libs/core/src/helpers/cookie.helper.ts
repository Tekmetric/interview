import { Request } from 'express';
import AppHelper from './app.helper';
import { Environment } from '../enums/environment.enum';
import { AppConfigKey } from '../enums/app-config-key.enum';

class CookieHelperClass {
  private static instance: CookieHelperClass;

  private constructor() {
    //
  }

  public static getInstance() {
    if (!CookieHelperClass.instance) {
      CookieHelperClass.instance = new CookieHelperClass();
    }
    return CookieHelperClass.instance;
  }

  public get(request: Request, cookieName: string): string {
    try {
      const cookie = request.headers.cookie
        .split(';')
        .find((cookie) => cookie.match(cookieName));

      if (cookie) {
        return cookie.split('=')[1];
      }

      return null;
    } catch (_error) {
      return null;
    }
  }

  set(
    req: Request,
    {
      cookie,
      value,
      expires,
      path,
    }: { cookie: string; value: string; expires: Date; path?: string },
  ) {
    const isDevelopment = AppHelper.checkEnvironment(Environment.Development);

    req.res.cookie(cookie, value, {
      expires,
      httpOnly: true,
      secure: !isDevelopment,
      domain: this.getDefaultLinkDomain(),
      path: path ?? '/',
      sameSite: 'lax',
    });
  }

  clear(req: Request, cookie: string) {
    req.res.clearCookie(cookie);
  }

  private getDefaultLinkDomain() {
    const domain = AppHelper.getConfig(AppConfigKey.DefaultLink).match(
      '.*([^.]+|)(com|net|org|info|coop|int|co.uk|org.uk|ac.uk|uk|ro)$|localhost',
    );

    return domain?.[0];
  }
}

const CookieHelper = CookieHelperClass.getInstance();

export default CookieHelper;
