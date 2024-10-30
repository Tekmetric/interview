import { ExecutionContext } from '@nestjs/common';
import { UserContext, getContext } from '@tekmetric/core';

export const getCurrentUser = (context: ExecutionContext): UserContext => {
  const ctx = getContext(context);

  return ctx.req.user as UserContext;
};
