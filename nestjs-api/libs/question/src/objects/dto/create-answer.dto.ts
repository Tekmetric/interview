import { ArgsType, Field } from '@nestjs/graphql';
import { MinLength } from 'class-validator';

@ArgsType()
export class CreateAnswerDto {
  @Field()
  questionId: string;

  @Field()
  @MinLength(5)
  description: string;
}
