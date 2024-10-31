import { ArgsType, Field } from '@nestjs/graphql';
import { MinLength } from 'class-validator';

@ArgsType()
export class CreateQuestionDto {
  @Field()
  @MinLength(5)
  title: string;

  @Field()
  @MinLength(5)
  description: string;
}
