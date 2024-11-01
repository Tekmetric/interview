import { UseGuards } from '@nestjs/common';
import {
  Args,
  Mutation,
  Parent,
  Query,
  ResolveField,
  Resolver,
} from '@nestjs/graphql';
import { CurrentUser, IsAdmin, IsAdminGuard } from '@tekmetric/auth';
import { UserContext } from '@tekmetric/core';
import { QuestionStatus } from '@tekmetric/database';
import { CreateQuestionDto } from '../objects/dto/create-question.dto';
import { Answer } from '../objects/models/answer.model';
import { Author } from '../objects/models/author.model';
import { Question } from '../objects/models/question.model';
import { QuestionService } from '../services/question.service';
import { QuestionPermissions } from '../objects/models/question-permissions.model';

@UseGuards(IsAdminGuard)
@Resolver(() => Question)
export class QuestionResolver {
  constructor(private readonly service: QuestionService) {}

  @Query(() => [Question])
  public async questions(
    @Args('status', { type: () => QuestionStatus }) status: QuestionStatus,
  ) {
    return await this.service.getQuestionsByStatus(status);
  }

  @Query(() => Question)
  public async question(@Args('id', { type: () => String }) id: string) {
    return await this.service.getQuestionById(id);
  }

  @ResolveField(() => String)
  public async shortDescription(@Parent() question: Question) {
    return await this.service.getShortDescription(question);
  }

  @ResolveField(() => Author)
  public async author(@Parent() question: Question) {
    return await this.service.getAuthorById(question.authorId);
  }

  @ResolveField(() => [Answer])
  public async answers(@Parent() question: Question) {
    return await this.service.getAnswersByQuestionId(question.id);
  }

  @ResolveField(() => QuestionPermissions)
  public async permissions(
    @Parent() question: Question,
    @CurrentUser() user: UserContext,
  ) {
    return {
      id: question.id,
      canResolve: user.role === 'ADMIN',
    };
  }

  @Mutation(() => Question)
  async createQuestion(
    @CurrentUser() user: UserContext,
    @Args({ name: 'input', type: () => CreateQuestionDto })
    question: CreateQuestionDto,
  ) {
    return await this.service.createQuestion(user.userId, question);
  }

  @IsAdmin()
  @Mutation(() => Question)
  async resolveQuestion(@Args('id', { type: () => String }) id: string) {
    return await this.service.resolveQuestion(id);
  }
}
