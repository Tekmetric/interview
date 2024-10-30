import { Injectable, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import {
  AuthPayload,
  CookieHelper,
  CryptoHelper,
  DateHelper,
} from '@tekmetric/core';
import { Request } from 'express';
import { AUTH_COOKIE_KEYS } from '../enums/auth-cookie-keys.enum';
import { Profile } from '../objects/models/profile.model';
import { AuthRepository } from '../repositories/auth.repository';

@Injectable()
export class AuthService {
  constructor(
    private readonly repo: AuthRepository,
    private readonly jwtService: JwtService,
  ) {
    //
  }

  public async login(
    request: Request,
    { email, password }: { email: string; password: string },
  ) {
    const { user } = await this.getAuthData(email);

    if (!user || !user.authentication) {
      this.logout(request);

      throw new UnauthorizedException();
    }

    const isPasswordValid = CryptoHelper.compare(
      password,
      user.authentication.password,
    );

    if (!isPasswordValid) {
      this.logout(request);

      throw new UnauthorizedException();
    }

    this.setAuthSession(request, {
      userId: user.id,
      role: user.role,
    });

    return await this.getProfile(user.id);
  }

  public logout(request: Request) {
    request['user'] = null;

    CookieHelper.clear(request, AUTH_COOKIE_KEYS.AccessToken);
  }

  public setAuthSession(request: Request, payload: AuthPayload) {
    const access_token = this.generateAccessToken({
      userId: payload.userId,
      role: payload.role,
    });

    this.setAuthCookie(request, access_token);

    return access_token;
  }

  public async getProfile(userId: string): Promise<Profile> {
    const user = await this.repo.getProfile(userId);

    if (!user?.id) {
      throw new UnauthorizedException();
    }

    const fullName = [user.firstName, user.lastName].join(' ');

    return {
      userId: user.id,
      fullName: fullName,
      role: user.role,
    };
  }

  private generateAccessToken(payload: AuthPayload) {
    return this.jwtService.sign(payload);
  }

  private setAuthCookie(request: Request, token: string) {
    const expires = DateHelper.addDays('1');

    CookieHelper.set(request, {
      cookie: AUTH_COOKIE_KEYS.AccessToken,
      expires,
      value: token,
    });
  }

  private async getAuthData(email: string) {
    const user = await this.repo.getAuthData(email);

    if (!user) {
      return { user: null };
    }

    return { user };
  }
}
