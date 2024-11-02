import {
  Injectable,
  NestInterceptor,
  ExecutionContext,
  CallHandler,
} from '@nestjs/common';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { getContext } from '@tekmetric/core';
import { Reflector } from '@nestjs/core';
import { IS_PUBLIC_KEY } from '../decorators/is-public.decorator';
import { getCurrentUser } from '../helpers/get-current-user';
import { AuthService } from '../services/auth.service';

export interface Response<T> {
  data: T;
}

@Injectable()
export class AuthInterceptor<T> implements NestInterceptor<T, Response<T>> {
  constructor(
    private readonly reflector: Reflector,
    private readonly authService: AuthService,
  ) {}

  public intercept(
    context: ExecutionContext,
    next: CallHandler,
  ): Observable<Response<T>> {
    return next.handle().pipe(
      map((response) => {
        const isPublic = this.reflector.getAllAndOverride<boolean>(
          IS_PUBLIC_KEY,
          [context.getHandler(), context.getClass()],
        );

        if (isPublic) {
          return response;
        }

        const user = getCurrentUser(context);
        const { req } = getContext(context);

        if (!user) {
          this.authService.logout(req);
        } else {
          this.authService.setAuthSession(req, user);
        }

        return response;
      }),
    );
  }
}
