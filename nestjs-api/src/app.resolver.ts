import { Public } from '@tekmetric/auth';
import { Query, Resolver } from '@nestjs/graphql';
import { AppService } from './app.service';

@Resolver()
export class AppResolver {
  constructor(private readonly appService: AppService) {}

  @Public()
  @Query(() => String)
  async hello() {
    return await this.appService.getHello();
  }
}
