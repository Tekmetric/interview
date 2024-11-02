import { UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { Test, TestingModule } from '@nestjs/testing';
import { CookieHelper, CryptoHelper, DateHelper } from '@tekmetric/core';
import { UserRole } from '@tekmetric/database';
import { Request } from 'express';
import { AUTH_COOKIE_KEYS } from '../enums/auth-cookie-keys.enum';
import { AuthRepository } from '../repositories/auth.repository';
import { AuthService } from './auth.service';

jest.mock('@tekmetric/core', () => ({
  CookieHelper: {
    clear: jest.fn(),
    set: jest.fn(),
  },
  CryptoHelper: {
    compare: jest.fn(),
  },
  DateHelper: {
    addDays: jest.fn(),
  },
}));

describe('AuthService', () => {
  let authService: AuthService;
  let authRepository: AuthRepository;
  let jwtService: JwtService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthService,
        {
          provide: AuthRepository,
          useValue: {
            getAuthData: jest.fn(),
            getProfile: jest.fn(),
          },
        },
        {
          provide: JwtService,
          useValue: {
            sign: jest.fn(),
          },
        },
      ],
    }).compile();

    authService = module.get<AuthService>(AuthService);
    authRepository = module.get<AuthRepository>(AuthRepository);
    jwtService = module.get<JwtService>(JwtService);
  });

  describe('login', () => {
    it('should login successfully', async () => {
      const request = {} as Request;
      const email = 'test@example.com';
      const password = 'password';

      const user = {
        id: '1',
        role: UserRole.ADMIN,
        authentication: {
          password: 'hashedPassword',
        },
        createdAt: new Date(),
        updatedAt: new Date(),
        email: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
      };

      const profile = {
        userId: '1',
        fullName: 'John Doe',
        role: UserRole.USER,
      };

      jest.spyOn(authRepository, 'getAuthData').mockResolvedValue(user);
      jest.spyOn(CryptoHelper, 'compare').mockReturnValue(true);
      jest.spyOn(authService, 'getProfile').mockResolvedValue(profile);
      jest.spyOn(authService, 'setAuthSession').mockReturnValue('accessToken');

      const result = await authService.login(request, { email, password });

      expect(authRepository.getAuthData).toHaveBeenCalledWith(email);
      expect(CryptoHelper.compare).toHaveBeenCalledWith(
        password,
        user.authentication.password,
      );
      expect(authService.setAuthSession).toHaveBeenCalledWith(request, {
        userId: user.id,
        role: user.role,
      });
      expect(authService.getProfile).toHaveBeenCalledWith(user.id);
      expect(result).toEqual(profile);
    });

    it('should throw UnauthorizedException if user not found', async () => {
      const request = {} as Request;
      const email = 'test@example.com';
      const password = 'password';

      jest.spyOn(authRepository, 'getAuthData').mockResolvedValue(null);
      const logoutSpy = jest
        .spyOn(authService, 'logout')
        .mockImplementation(() => {});

      await expect(
        authService.login(request, { email, password }),
      ).rejects.toThrow(UnauthorizedException);

      expect(logoutSpy).toHaveBeenCalledWith(request);
    });

    it('should throw UnauthorizedException if password is invalid', async () => {
      const request = {} as Request;
      const email = 'test@example.com';
      const password = 'password';

      const user = {
        id: '1',
        role: UserRole.ADMIN,
        authentication: {
          password: 'hashedPassword',
        },
        createdAt: new Date(),
        updatedAt: new Date(),
        email: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
      };

      jest.spyOn(authRepository, 'getAuthData').mockResolvedValue(user);
      jest.spyOn(CryptoHelper, 'compare').mockReturnValue(false);
      const logoutSpy = jest
        .spyOn(authService, 'logout')
        .mockImplementation(() => {});

      await expect(
        authService.login(request, { email, password }),
      ).rejects.toThrow(UnauthorizedException);

      expect(logoutSpy).toHaveBeenCalledWith(request);
    });
  });

  describe('logout', () => {
    it('should clear user session and cookies', () => {
      const request = {} as Request;

      authService.logout(request);

      expect(request['user']).toBeNull();
      expect(CookieHelper.clear).toHaveBeenCalledWith(
        request,
        AUTH_COOKIE_KEYS.AccessToken,
      );
    });
  });

  describe('setAuthSession', () => {
    it('should set auth session and cookies', () => {
      const request = {} as Request;
      const payload = { userId: '1', role: UserRole.ADMIN };
      const token = 'accessToken';

      jest.spyOn(jwtService, 'sign').mockReturnValue(token);
      jest.spyOn(DateHelper, 'addDays').mockReturnValue('expires date' as any);

      const result = authService.setAuthSession(request, payload);

      expect(jwtService.sign).toHaveBeenCalledWith(payload);

      expect(CookieHelper.set).toHaveBeenCalledWith(request, {
        cookie: AUTH_COOKIE_KEYS.AccessToken,
        expires: 'expires date',
        value: token,
      });
      expect(result).toBe(token);
    });
  });

  describe('getProfile', () => {
    it('should return user profile', async () => {
      const userId = '1';
      const user = {
        id: userId,
        firstName: 'John',
        lastName: 'Doe',
        role: UserRole.USER,
        createdAt: new Date(),
        updatedAt: new Date(),
        email: 'john.doe@example.com',
      };
      const profile = {
        userId: userId,
        fullName: 'John Doe',
        role: UserRole.USER,
      };

      jest.spyOn(authRepository, 'getProfile').mockResolvedValue(user);

      const result = await authService.getProfile(userId);

      expect(authRepository.getProfile).toHaveBeenCalledWith(userId);
      expect(result).toEqual(profile);
    });

    it('should throw UnauthorizedException if user not found', async () => {
      const userId = '1';

      jest.spyOn(authRepository, 'getProfile').mockResolvedValue(null);

      await expect(authService.getProfile(userId)).rejects.toThrow(
        UnauthorizedException,
      );
    });
  });
});
