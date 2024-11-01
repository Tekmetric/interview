import { Field, InputType } from '@nestjs/graphql';
import { MinLength } from 'class-validator';

@InputType()
export class CreateAnswerDto {
  @Field()
  questionId: string;

  @Field()
  @MinLength(5)
  description: string;
}
