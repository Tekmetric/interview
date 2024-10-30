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

  const user = await prisma.user.upsert({
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

  console.log({ user });
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
