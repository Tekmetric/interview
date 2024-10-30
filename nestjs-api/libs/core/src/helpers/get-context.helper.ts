import { ContextType, ExecutionContext } from '@nestjs/common';
import { GqlExecutionContext } from '@nestjs/graphql';

export const getContext = (context: ExecutionContext) => {
  const requestType = context.getType<ContextType | 'graphql'>();

  if (requestType === 'graphql') {
    const gqlCtx = GqlExecutionContext.create(context);
    const ctx = gqlCtx.getContext();

    return { req: ctx.req, res: ctx.req.res };
  }

  return {
    req: context.switchToHttp().getRequest(),
    res: context.switchToHttp().getResponse(),
  };
};
