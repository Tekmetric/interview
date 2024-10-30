import { AuthModule } from '@tekmetric/auth';
import { AppConfigKey, AppHelper, Environment } from '@tekmetric/core';
import { PrismaModule } from '@tekmetric/database';
import { ApolloDriver, ApolloDriverConfig } from '@nestjs/apollo';
import { Module } from '@nestjs/common';
import { GraphQLModule } from '@nestjs/graphql';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { AppResolver } from './app.resolver';

@Module({
  imports: [
    PrismaModule,
    AuthModule,
    GraphQLModule.forRootAsync<ApolloDriverConfig>({
      driver: ApolloDriver,
      useFactory: () => {
        return {
          autoSchemaFile: 'schema.gql',
          sortSchema: true,
          cors: {
            origin: AppHelper.getConfig(AppConfigKey.DefaultLink),
            credentials: true,
          },
          debug: AppHelper.checkEnvironment(Environment.Development),
          playground: AppHelper.checkEnvironment(Environment.Development)
            ? { settings: { 'request.credentials': 'include' } }
            : false,
        };
      },
    }),
  ],
  controllers: [AppController],
  providers: [AppService, AppResolver],
})
export class AppModule {}
