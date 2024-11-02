import { Module } from '@nestjs/common';
import { PrismaModule } from '@tekmetric/database';
import { UserService } from './services/user.service';

@Module({
  imports: [PrismaModule],
  providers: [UserService],
  exports: [UserService],
})
export class UserModule {}
