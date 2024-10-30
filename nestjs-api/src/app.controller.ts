import { Controller, Get } from '@nestjs/common';
import { Public } from '@tekmetric/auth';
import { AppService } from './app.service';

@Controller()
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Get()
  @Public()
  async getHello(): Promise<string> {
    return this.appService.getHello();
  }
}
