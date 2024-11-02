import { Module } from '@nestjs/common';
import { QuestionService } from './services/question.service';
import { QuestionRepository } from './repositories/question.repository';
import { PrismaModule } from '@tekmetric/database';
import { QuestionResolver } from './resolvers/question.resolver';
import { AnswerServices } from './services/answer.service';
import { AnswerRepository } from './repositories/answer.repository';
import { AnswerResolver } from './resolvers/answer.resolver';

@Module({
  imports: [PrismaModule],
  providers: [
    QuestionService,
    QuestionRepository,
    QuestionResolver,
    AnswerServices,
    AnswerRepository,
    AnswerResolver,
  ],
  exports: [QuestionService],
})
export class QuestionModule {}
