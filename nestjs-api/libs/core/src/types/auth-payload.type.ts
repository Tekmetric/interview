import { UserRole } from '@tekmetric/database';

export type AuthPayload = {
  userId: string;
  role: UserRole;
};
