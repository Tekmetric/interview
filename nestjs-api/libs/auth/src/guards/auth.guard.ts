import {
  AppConfigKey,
  AppHelper,
  AuthPayload,
  CookieHelper,
  getContext,
} from '@tekmetric/core';
import {
  CanActivate,
  ExecutionContext,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { JwtService } from '@nestjs/jwt';
import { Request } from 'express';
import { IS_PUBLIC_KEY } from '../decorators/is-public.decorator';
import { AUTH_COOKIE_KEYS } from '../enums/auth-cookie-keys.enum';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(
    private readonly jwtService: JwtService,
    private readonly reflector: Reflector,
  ) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const isPublic = this.reflector.getAllAndOverride<boolean>(IS_PUBLIC_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);

    if (isPublic) {
      return true;
    }

    const request = getContext(context)?.req;
    const token = this.extractTokenRequestCookie(request);

    if (!token) {
      throw new UnauthorizedException();
    }

    try {
      const payload = await this.jwtService.verifyAsync<AuthPayload>(token, {
        secret: AppHelper.getConfig(AppConfigKey.AuthSecret),
      });

      request['user'] = payload;
    } catch {
      request['user'] = null;

      throw new UnauthorizedException();
    }

    return true;
  }

  private extractTokenRequestCookie(request: Request): string | undefined {
    const accessCookie = CookieHelper.get(
      request,
      AUTH_COOKIE_KEYS.AccessToken,
    );

    if (!accessCookie) {
      return undefined;
    }

    return accessCookie;
  }
}
