import { Test, TestingModule } from '@nestjs/testing';
import { AuthResolver } from './auth.resolver';
import { AuthService } from '../services/auth.service';
import { GraphQLContext, UserContext } from '@tekmetric/core';
import { LoginDto } from '../objects/dto/login.dto';
import { Profile } from '../objects/models/profile.model';
import { UserRole } from '@tekmetric/database';

describe('AuthResolver', () => {
  let authResolver: AuthResolver;
  let authService: AuthService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthResolver,
        {
          provide: AuthService,
          useValue: {
            login: jest.fn(),
            logout: jest.fn(),
            getProfile: jest.fn(),
          },
        },
      ],
    }).compile();

    authResolver = module.get<AuthResolver>(AuthResolver);
    authService = module.get<AuthService>(AuthService);
  });

  describe('login', () => {
    it('should login successfully', async () => {
      const context = { req: {} } as GraphQLContext;
      const loginDto: LoginDto = {
        email: 'test@example.com',
        password: 'password',
      };
      const profile: Profile = {
        userId: '1',
        fullName: 'John Doe',
        role: UserRole.USER,
      };

      jest.spyOn(authService, 'login').mockResolvedValue(profile);

      const result = await authResolver.login(context, loginDto);

      expect(authService.login).toHaveBeenCalledWith(context.req, loginDto);
      expect(result).toEqual(profile);
    });
  });

  describe('logout', () => {
    it('should logout successfully', async () => {
      const context = { req: {} } as GraphQLContext;

      const result = await authResolver.logout(context);

      expect(authService.logout).toHaveBeenCalledWith(context.req);
      expect(result).toBeNull();
    });
  });

  describe('profile', () => {
    it('should return user profile', async () => {
      const user: UserContext = { userId: '1', role: UserRole.USER };
      const profile: Profile = {
        userId: '1',
        fullName: 'John Doe',
        role: UserRole.USER,
      };

      jest.spyOn(authService, 'getProfile').mockResolvedValue(profile);

      const result = await authResolver.profile(user);

      expect(authService.getProfile).toHaveBeenCalledWith(user.userId);
      expect(result).toEqual(profile);
    });
  });
});
