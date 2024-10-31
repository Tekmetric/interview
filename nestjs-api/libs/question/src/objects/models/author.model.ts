import { Field, ObjectType } from '@nestjs/graphql';

@ObjectType()
export class Author {
  @Field(() => String)
  id: string;

  @Field(() => String)
  firstName: string;

  @Field(() => String)
  lastName: string;
}
