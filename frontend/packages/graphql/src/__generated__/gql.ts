/* eslint-disable */
import * as types from './graphql';
import { TypedDocumentNode as DocumentNode } from '@graphql-typed-document-node/core';

/**
 * Map of all GraphQL operations in the project.
 *
 * This map has several performance disadvantages:
 * 1. It is not tree-shakeable, so it will include all operations in the project.
 * 2. It is not minifiable, so the string of a GraphQL query will be multiple times inside the bundle.
 * 3. It does not support dead code elimination, so it will add unused operations.
 *
 * Therefore it is highly recommended to use the babel or swc plugin for production.
 * Learn more about it here: https://the-guild.dev/graphql/codegen/plugins/presets/preset-client#reducing-bundle-size
 */
const documents = {
    "\n  mutation createAnswer($input: CreateAnswerDto!) {\n    createAnswer(input: $input) {\n      id\n      description\n    }\n  }\n": types.CreateAnswerDocument,
    "\n  mutation createQuestion($input: CreateQuestionDto!) {\n    createQuestion(input: $input) {\n      id\n      title\n      description\n    }\n  }\n": types.CreateQuestionDocument,
    "\n  query getQuestions($status: QuestionStatus!) {\n    questions(status: $status) {\n      id\n      ...Question\n    }\n  }\n\n  \n": types.GetQuestionsDocument,
    "\n  mutation login($email: String!, $password: String!) {\n    login(email: $email, password: $password) {\n      userId\n    }\n  }\n": types.LoginDocument,
    "\n  fragment Question on Question {\n    id\n    title\n    shortDescription\n    status\n    createdAt\n    author {\n      id\n      firstName\n      lastName\n    }\n    permissions {\n      id\n      canResolve\n    }\n  }\n": types.QuestionFragmentDoc,
    "\n  mutation resolveQuestion($id: String!) {\n    resolveQuestion(id: $id) {\n      id\n      status\n    }\n  }\n": types.ResolveQuestionDocument,
};

/**
 * The gql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 *
 *
 * @example
 * ```ts
 * const query = gql(`query GetUser($id: ID!) { user(id: $id) { name } }`);
 * ```
 *
 * The query argument is unknown!
 * Please regenerate the types.
 */
export function gql(source: string): unknown;

/**
 * The gql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function gql(source: "\n  mutation createAnswer($input: CreateAnswerDto!) {\n    createAnswer(input: $input) {\n      id\n      description\n    }\n  }\n"): (typeof documents)["\n  mutation createAnswer($input: CreateAnswerDto!) {\n    createAnswer(input: $input) {\n      id\n      description\n    }\n  }\n"];
/**
 * The gql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function gql(source: "\n  mutation createQuestion($input: CreateQuestionDto!) {\n    createQuestion(input: $input) {\n      id\n      title\n      description\n    }\n  }\n"): (typeof documents)["\n  mutation createQuestion($input: CreateQuestionDto!) {\n    createQuestion(input: $input) {\n      id\n      title\n      description\n    }\n  }\n"];
/**
 * The gql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function gql(source: "\n  query getQuestions($status: QuestionStatus!) {\n    questions(status: $status) {\n      id\n      ...Question\n    }\n  }\n\n  \n"): (typeof documents)["\n  query getQuestions($status: QuestionStatus!) {\n    questions(status: $status) {\n      id\n      ...Question\n    }\n  }\n\n  \n"];
/**
 * The gql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function gql(source: "\n  mutation login($email: String!, $password: String!) {\n    login(email: $email, password: $password) {\n      userId\n    }\n  }\n"): (typeof documents)["\n  mutation login($email: String!, $password: String!) {\n    login(email: $email, password: $password) {\n      userId\n    }\n  }\n"];
/**
 * The gql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function gql(source: "\n  fragment Question on Question {\n    id\n    title\n    shortDescription\n    status\n    createdAt\n    author {\n      id\n      firstName\n      lastName\n    }\n    permissions {\n      id\n      canResolve\n    }\n  }\n"): (typeof documents)["\n  fragment Question on Question {\n    id\n    title\n    shortDescription\n    status\n    createdAt\n    author {\n      id\n      firstName\n      lastName\n    }\n    permissions {\n      id\n      canResolve\n    }\n  }\n"];
/**
 * The gql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function gql(source: "\n  mutation resolveQuestion($id: String!) {\n    resolveQuestion(id: $id) {\n      id\n      status\n    }\n  }\n"): (typeof documents)["\n  mutation resolveQuestion($id: String!) {\n    resolveQuestion(id: $id) {\n      id\n      status\n    }\n  }\n"];

export function gql(source: string) {
  return (documents as any)[source] ?? {};
}

export type DocumentType<TDocumentNode extends DocumentNode<any, any>> = TDocumentNode extends DocumentNode<  infer TType,  any>  ? TType  : never;