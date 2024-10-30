import { Injectable } from '@nestjs/common';
import { PrismaService } from '@tekmetric/database';

@Injectable()
export class UserService {
  constructor(private readonly prismaService: PrismaService) {
    //
  }
}
