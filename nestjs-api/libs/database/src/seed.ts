import * as dotenv from 'dotenv';
import { UserRole, PrismaClient } from './generated';
import { AppConfigKey, AppHelper, CryptoHelper } from '../../core/src';
dotenv.config();

const prisma = new PrismaClient();

async function main() {
  const email = AppHelper.getConfig(AppConfigKey.DefaultUsername);
  const password = CryptoHelper.hash(
    AppHelper.getConfig(AppConfigKey.DefaultUserPassword),
  );

  const totalUsers = await prisma.user.count();

  if (totalUsers > 0) {
    return;
  }

  const admin = await prisma.user.upsert({
    where: { email },
    update: {},
    create: {
      email,
      firstName: 'Sergiu',
      lastName: 'Butnarasu',
      role: UserRole.ADMIN,
      authentication: {
        create: {
          password,
        },
      },
    },
  });

  console.log({ admin });

  const user = await prisma.user.upsert({
    where: { email: 'sergiu@butnarasu.ro' },
    update: {},
    create: {
      email: 'sergiu@butnarasu.ro',
      firstName: 'Sergiu',
      lastName: 'User',
      role: UserRole.USER,
      authentication: {
        create: {
          password,
        },
      },
    },
  });

  console.log({ user });

  const question = await prisma.question.createMany({
    data: [
      {
        title: 'How to create a new question?',
        description:
          'I want to create a new question, but I do not know how to do it.',
        authorId: user.id,
        status: 'PENDING',
      },
      {
        title: 'Can I create a new question?',
        description:
          'I want to create a new question, but I do not know if I can do it.',
        authorId: user.id,
        status: 'PENDING',
      },
    ],
  });

  console.log({ question });
}

main()
  .then(async () => {
    await prisma.$disconnect();
  })
  .catch(async (e) => {
    console.error(e);
    await prisma.$disconnect();
    process.exit(1);
  });
