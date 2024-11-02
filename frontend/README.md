## Description

The front-end application is built using the Next.js framework. It leverages Apollo Client for GraphQL queries and mutations.

The application provides a user interface for managing a Q&A application.

## Tech stack

- Nextjs
- turborepo (monorepo)
- Apollo GraphQL Client
- zod (schema validation)
- Tailwind CSS
- TypeScript
- Jest
- React Testing Library
- ESLint
- Prettier
- react-toastify (notifications)

## Pre-requisites

- nvm
- yarn

## Installation

```bash
# use the correct node version
$ nvm use

# Install the dependencies
$ yarn install
```

## Environment variables

Adjust the environment variables in the `.env` file from the `/apps/web/.env`.
For demo purposes, you can use this configuration:

```
NEXT_PUBLIC_TEKMETRIC_API_URL=http://localhost:5089
```

## Running the app

```bash
# watch mode
$ yarn run dev

# The application will be available at http://localhost:3000/
```

## Test

```bash
$ yarn run test
```

## Type check

```bash
$ yarn run typecheck
```

## Lint

```bash
$ yarn run lint
```

## Generate graphql types

```bash
$ yarn run generate:types
```
