import * as dotenv from 'dotenv';
dotenv.config();

import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { AppHelper, AppConfigKey, Environment } from '@tekmetric/core';
import helmet from 'helmet';
import { ValidationPipe } from '@nestjs/common';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  const port = AppHelper.getConfig(AppConfigKey.Port);
  const isDevelopment = AppHelper.checkEnvironment(Environment.Development);

  app.enableCors({
    credentials: true,
    origin: AppHelper.getConfig(AppConfigKey.DefaultLink),
  });

  app.useGlobalPipes(new ValidationPipe({ transform: true }));

  if (!isDevelopment) {
    app.use(helmet());
  }

  await app.listen(port ?? 5089);
}
bootstrap();
