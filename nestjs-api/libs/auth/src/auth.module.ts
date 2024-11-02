import { AppConfigKey, AppHelper } from '@tekmetric/core';
import { PrismaModule } from '@tekmetric/database';
import { Module } from '@nestjs/common';
import { APP_GUARD, APP_INTERCEPTOR } from '@nestjs/core';
import { JwtModule } from '@nestjs/jwt';
import { AuthController } from './controllers/auth.controller';
import { AuthGuard } from './guards/auth.guard';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { AuthResolver } from './resolvers/auth.resolver';
import { AuthService } from './services/auth.service';
import { AuthRepository } from './repositories/auth.repository';

@Module({
  imports: [
    PrismaModule,
    JwtModule.register({
      global: true,
      secret: AppHelper.getConfig(AppConfigKey.AuthSecret),
      signOptions: {
        expiresIn: AppHelper.getConfig(AppConfigKey.AuthTokenExpiresIn) ?? '1m',
      },
    }),
  ],
  controllers: [AuthController],
  providers: [
    {
      provide: APP_GUARD,
      useClass: AuthGuard,
    },
    {
      provide: APP_INTERCEPTOR,
      useClass: AuthInterceptor,
    },
    AuthRepository,
    AuthService,
    AuthResolver,
  ],
})
export class AuthModule {}
