import { PrismaService } from '@tekmetric/database';
import { Injectable } from '@nestjs/common';

@Injectable()
export class AuthRepository {
  constructor(private readonly prismaService: PrismaService) {
    //
  }

  public async getAuthData(email: string) {
    const user = await this.prismaService.user.findUnique({
      include: { authentication: { select: { password: true } } },
      where: { email },
    });

    return user;
  }

  public async getProfile(userId: string) {
    const user = await this.prismaService.user.findUnique({
      where: {
        id: userId,
      },
    });

    return user;
  }
}
