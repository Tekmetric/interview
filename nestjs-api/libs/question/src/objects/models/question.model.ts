import {
  Field,
  GraphQLISODateTime,
  ObjectType,
  registerEnumType,
} from '@nestjs/graphql';
import { QuestionStatus } from '@tekmetric/database';

registerEnumType(QuestionStatus, {
  name: 'QuestionStatus',
});

@ObjectType()
export class Question {
  @Field(() => String)
  id: string;

  @Field(() => QuestionStatus)
  status: QuestionStatus;

  @Field(() => String)
  authorId: string;

  @Field(() => String)
  title: string;

  @Field(() => String)
  description: string;

  @Field(() => GraphQLISODateTime)
  createdAt: Date;
}
