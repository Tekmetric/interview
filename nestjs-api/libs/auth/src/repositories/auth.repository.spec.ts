import { Test, TestingModule } from '@nestjs/testing';
import { PrismaService, UserRole } from '@tekmetric/database';
import { AuthRepository } from './auth.repository';

const email = 'test@example.com';
const user = {
  id: '1',
  email,
  role: UserRole.USER,
  createdAt: new Date(),
  updatedAt: new Date(),
  firstName: 'John',
  lastName: 'Doe',
  authentication: {
    password: 'hashedPassword',
  },
};

describe('AuthRepository', () => {
  let authRepository: AuthRepository;
  let prismaService: PrismaService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthRepository,
        {
          provide: PrismaService,
          useValue: {
            user: {
              findUnique: jest.fn(),
            },
          },
        },
      ],
    }).compile();

    authRepository = module.get<AuthRepository>(AuthRepository);
    prismaService = module.get<PrismaService>(PrismaService);
  });

  describe('getAuthData', () => {
    it('should return user authentication data', async () => {
      jest.spyOn(prismaService.user, 'findUnique').mockResolvedValue(user);

      const result = await authRepository.getAuthData(email);

      expect(prismaService.user.findUnique).toHaveBeenCalledWith({
        include: { authentication: { select: { password: true } } },
        where: { email },
      });
      expect(result).toEqual(user);
    });

    it('should return null if user not found', async () => {
      const email = 'test@example.com';

      jest.spyOn(prismaService.user, 'findUnique').mockResolvedValue(null);

      const result = await authRepository.getAuthData(email);

      expect(prismaService.user.findUnique).toHaveBeenCalledWith({
        include: { authentication: { select: { password: true } } },
        where: { email },
      });
      expect(result).toBeNull();
    });
  });

  describe('getProfile', () => {
    it('should return user profile', async () => {
      const userId = '1';

      jest.spyOn(prismaService.user, 'findUnique').mockResolvedValue(user);

      const result = await authRepository.getProfile(userId);

      expect(prismaService.user.findUnique).toHaveBeenCalledWith({
        where: { id: userId },
      });
      expect(result).toEqual(user);
    });

    it('should return null if user not found', async () => {
      const userId = '1';

      jest.spyOn(prismaService.user, 'findUnique').mockResolvedValue(null);

      const result = await authRepository.getProfile(userId);

      expect(prismaService.user.findUnique).toHaveBeenCalledWith({
        where: { id: userId },
      });
      expect(result).toBeNull();
    });
  });
});
