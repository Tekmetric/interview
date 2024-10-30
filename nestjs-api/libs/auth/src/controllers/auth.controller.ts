import { UserContext } from '@tekmetric/core';
import {
  Body,
  Controller,
  Get,
  HttpCode,
  HttpStatus,
  Post,
  Request,
} from '@nestjs/common';
import { Request as RequestType } from 'express';
import { CurrentUser } from '../decorators/current-user.decorator';
import { Public } from '../decorators/is-public.decorator';
import { LoginDto } from '../objects/dto/login.dto';
import { AuthService } from '../services/auth.service';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Public()
  @HttpCode(HttpStatus.OK)
  @Post('login')
  public async login(@Request() req: RequestType, @Body() signInDto: LoginDto) {
    return await this.authService.login(req, signInDto);
  }

  @Post('logout')
  public async logout(@Request() req: RequestType) {
    this.authService.logout(req);

    return null;
  }

  @Get('profile')
  public async getProfile(@CurrentUser() user: UserContext) {
    return await this.authService.getProfile(user.userId);
  }
}
