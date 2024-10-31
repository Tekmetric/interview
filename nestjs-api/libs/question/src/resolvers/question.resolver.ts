import {
  Args,
  Mutation,
  Parent,
  Query,
  ResolveField,
  Resolver,
} from '@nestjs/graphql';
import { CurrentUser } from '@tekmetric/auth';
import { UserContext } from '@tekmetric/core';
import { CreateQuestionDto } from '../objects/dto/create-question.dto';
import { Answer } from '../objects/models/answer.model';
import { Author } from '../objects/models/author.model';
import { Question } from '../objects/models/question.model';
import { QuestionService } from '../services/question.service';

@Resolver(() => Question)
export class QuestionResolver {
  constructor(private readonly service: QuestionService) {}

  @Query(() => [Question])
  public async questions() {
    return await this.service.getQuestions();
  }

  @Query(() => Question)
  public async question(@Args('id', { type: () => String }) id: string) {
    return await this.service.getQuestionById(id);
  }

  @ResolveField(() => Author)
  public async author(@Parent() question: Question) {
    return await this.service.getAuthorById(question.authorId);
  }

  @ResolveField(() => [Answer])
  public async answers(@Parent() question: Question) {
    return await this.service.getAnswersByQuestionId(question.id);
  }

  @Mutation(() => Question)
  async createQuestion(
    @CurrentUser() user: UserContext,
    @Args() question: CreateQuestionDto,
  ) {
    return await this.service.createQuestion(user.userId, question);
  }
}
