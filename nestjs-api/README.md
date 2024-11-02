## Description

The back-end application, named `nestjs-api`, is built using the [Nest](https://github.com/nestjs/nest) framework. It leverages Docker for managing the PostgreSQL database and Prisma as the ORM for database interactions. The application provides a GraphQL API with JWT cookie authentication for secure access.

## Tech stack

- NestJS
- PostgreSQL
- Prisma
- Docker
- GraphQL
- JWT cookie authentication
- Jest
- ESLint
- Prettier

## Pre-requisites

- docker
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

Adjust the environment variables in the `.env` file (`/.env`).
For demo purposes, you can use this configuration:

```
TEKMETRIC_AUTH_SECRET="3yC!bMOl0mA@JU9PqKB*v"
TEKMETRIC_AUTH_TOKEN_EXPIRES_IN="7d"
TEKMETRIC_DB_URL="postgres://sergiu:P4ssword@localhost:5432/postgres"
TEKMETRIC_DEFAULT_LINK="http://localhost:3000"
TEKMETRIC_DEFAULT_USERNAME="you@email.com"
TEKMETRIC_DEFAULT_USER_PASSWORD="P4ssword!"
TEKMETRIC_ENVIRONMENT="development"
TEKMETRIC_DEV_DB_USER="sergiu"
TEKMETRIC_DEV_DB_PASSWORD="P4ssword"
```

## Prepare the database

```bash
# start the database docker container
$ docker compose up -d

# generate the prisma client
$ yarn generate:prisma

# generate the database schema and run migrations
$ yarn db:deploy

# seed the database
$ yarn db:seed
```

## Running the app

```bash
# watch mode
$ yarn run start:dev

# The application will be available at http://localhost:5089
```

## Access the GraphQL playground

- http://localhost:5089/graphql

## Test

```bash
# unit tests
$ yarn run test

# e2e tests
$ yarn run test:e2e
```

## Generate migrations

```bash
$ yarn run generate:migration title
```
