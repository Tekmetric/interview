import { GraphQLExecutionContext } from '@nestjs/graphql';
import { Request } from 'express';

export type GraphQLContext = GraphQLExecutionContext & {
  req: Request;
};
