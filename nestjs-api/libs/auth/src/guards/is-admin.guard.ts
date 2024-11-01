import { CanActivate, ExecutionContext, Injectable } from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { UserRole } from '@tekmetric/database';
import { IS_ADMIN_KEY } from '../decorators/is-admin.decorator';
import { getCurrentUser } from '../helpers/get-current-user';

@Injectable()
export class IsAdminGuard implements CanActivate {
  constructor(private readonly reflector: Reflector) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const isAdminDecorator = this.reflector.getAllAndOverride<boolean>(
      IS_ADMIN_KEY,
      [context.getHandler(), context.getClass()],
    );

    if (!isAdminDecorator) {
      return true;
    }

    const user = getCurrentUser(context);

    return Boolean(user.role === UserRole.ADMIN);
  }
}
