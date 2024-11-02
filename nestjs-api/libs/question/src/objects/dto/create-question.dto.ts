import { Field, InputType } from '@nestjs/graphql';
import { MinLength } from 'class-validator';

@InputType()
export class CreateQuestionDto {
  @Field()
  @MinLength(5)
  title: string;

  @Field()
  @MinLength(5)
  description: string;
}
