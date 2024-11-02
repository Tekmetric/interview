import { GraphQLContext, UserContext } from '@tekmetric/core';
import { Args, Context, Mutation, Query, Resolver } from '@nestjs/graphql';
import { CurrentUser } from '../decorators/current-user.decorator';
import { Public } from '../decorators/is-public.decorator';
import { LoginDto } from '../objects/dto/login.dto';
import { Profile } from '../objects/models/profile.model';
import { AuthService } from '../services/auth.service';

@Resolver()
export class AuthResolver {
  constructor(private readonly authService: AuthService) {}

  @Public()
  @Mutation(() => Profile, { nullable: true })
  async login(
    @Context() context: GraphQLContext,
    @Args() { email, password }: LoginDto,
  ) {
    return await this.authService.login(context.req, {
      email,
      password,
    });
  }

  @Mutation(() => String, { nullable: true })
  async logout(@Context() context: GraphQLContext) {
    this.authService.logout(context.req);

    return null;
  }

  @Query(() => Profile)
  async profile(@CurrentUser() user: UserContext) {
    return await this.authService.getProfile(user.userId);
  }
}
