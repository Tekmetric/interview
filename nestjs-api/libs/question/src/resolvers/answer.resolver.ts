import {
  Args,
  Mutation,
  Parent,
  ResolveField,
  Resolver,
} from '@nestjs/graphql';
import { CurrentUser } from '@tekmetric/auth';
import { UserContext } from '@tekmetric/core';
import { CreateAnswerDto } from '../objects/dto/create-answer.dto';
import { Answer } from '../objects/models/answer.model';
import { Author } from '../objects/models/author.model';
import { AnswerServices } from '../services/answer.service';

@Resolver(() => Answer)
export class AnswerResolver {
  constructor(private readonly service: AnswerServices) {}

  @ResolveField(() => Author)
  public async author(@Parent() answer: Answer) {
    return await this.service.getAuthorById(answer.authorId);
  }

  @Mutation(() => Answer)
  async createAnswer(
    @CurrentUser() user: UserContext,
    @Args() Answer: CreateAnswerDto,
  ) {
    return await this.service.createAnswer(user.userId, Answer);
  }
}
