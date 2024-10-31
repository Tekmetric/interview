import { Field, GraphQLISODateTime, ObjectType } from '@nestjs/graphql';

@ObjectType()
export class Question {
  @Field(() => String)
  id: string;

  @Field(() => String)
  authorId: string;

  @Field(() => String)
  title: string;

  @Field(() => String)
  description: string;

  @Field(() => GraphQLISODateTime)
  createdAt: Date;
}
