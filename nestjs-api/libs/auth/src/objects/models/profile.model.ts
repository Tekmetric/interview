import { Field, ObjectType, registerEnumType } from '@nestjs/graphql';
import { UserRole } from '@tekmetric/database';

registerEnumType(UserRole, {
  name: 'UserRole',
});

@ObjectType()
export class Profile {
  @Field(() => String)
  userId: string;

  @Field(() => String)
  fullName: string;

  @Field(() => UserRole, { nullable: true })
  role?: UserRole;
}
