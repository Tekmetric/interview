export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
export type MakeEmpty<T extends { [key: string]: unknown }, K extends keyof T> = { [_ in K]?: never };
export type Incremental<T> = T | { [P in keyof T]?: P extends ' $fragmentName' | '__typename' ? T[P] : never };
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: { input: string; output: string; }
  String: { input: string; output: string; }
  Boolean: { input: boolean; output: boolean; }
  Int: { input: number; output: number; }
  Float: { input: number; output: number; }
  DateTime: { input: any; output: any; }
};

export type Answer = {
  __typename?: 'Answer';
  author: Author;
  authorId: Scalars['String']['output'];
  createdAt: Scalars['DateTime']['output'];
  description: Scalars['String']['output'];
  id: Scalars['String']['output'];
};

export type Author = {
  __typename?: 'Author';
  firstName: Scalars['String']['output'];
  id: Scalars['String']['output'];
  lastName: Scalars['String']['output'];
};

export type CreateAnswerDto = {
  description: Scalars['String']['input'];
  questionId: Scalars['String']['input'];
};

export type CreateQuestionDto = {
  description: Scalars['String']['input'];
  title: Scalars['String']['input'];
};

export type Mutation = {
  __typename?: 'Mutation';
  createAnswer: Answer;
  createQuestion: Question;
  login?: Maybe<Profile>;
  logout?: Maybe<Scalars['String']['output']>;
  resolveQuestion: Question;
};


export type MutationCreateAnswerArgs = {
  input: CreateAnswerDto;
};


export type MutationCreateQuestionArgs = {
  input: CreateQuestionDto;
};


export type MutationLoginArgs = {
  email: Scalars['String']['input'];
  password: Scalars['String']['input'];
};


export type MutationResolveQuestionArgs = {
  id: Scalars['String']['input'];
};

export type Profile = {
  __typename?: 'Profile';
  fullName: Scalars['String']['output'];
  role: UserRole;
  userId: Scalars['String']['output'];
};

export type Query = {
  __typename?: 'Query';
  hello: Scalars['String']['output'];
  profile: Profile;
  question: Question;
  questions: Array<Question>;
};


export type QueryQuestionArgs = {
  id: Scalars['String']['input'];
};


export type QueryQuestionsArgs = {
  status: QuestionStatus;
};

export type Question = {
  __typename?: 'Question';
  answers: Array<Answer>;
  author: Author;
  authorId: Scalars['String']['output'];
  createdAt: Scalars['DateTime']['output'];
  description: Scalars['String']['output'];
  id: Scalars['String']['output'];
  permissions: QuestionPermissions;
  status: QuestionStatus;
  title: Scalars['String']['output'];
};

export type QuestionPermissions = {
  __typename?: 'QuestionPermissions';
  canResolve: Scalars['Boolean']['output'];
  id: Scalars['String']['output'];
};

export enum QuestionStatus {
  Completed = 'COMPLETED',
  Pending = 'PENDING'
}

export enum UserRole {
  Admin = 'ADMIN',
  User = 'USER'
}

export type LoginMutationVariables = Exact<{
  email: Scalars['String']['input'];
  password: Scalars['String']['input'];
}>;


export type LoginMutation = { __typename?: 'Mutation', login?: { __typename?: 'Profile', userId: string } | null };
