import { Field, ObjectType } from '@nestjs/graphql';

@ObjectType()
export class QuestionPermissions {
  @Field(() => String)
  id: string;

  @Field(() => Boolean)
  canResolve: boolean;
}
