import type { QueryClient } from '@tanstack/react-query';
import { createRootRouteWithContext, Link, Outlet } from '@tanstack/react-router';
import { TanStackRouterDevtools } from '@tanstack/router-devtools';

export const Route = createRootRouteWithContext<{ queryClient: QueryClient }>()({
  wrapInSuspense: true,
  component: () => (
    <>
      <div className="p-2 flex gap-2">
        <Link to="/" activeProps={ { className: 'bold' } }>
          Home
        </Link>
        <Link to="/query" activeProps={ { className: 'bold' } }>
          Query
        </Link>
        <Link
          to="/metro/$stationCodes"
          params={ {
            stationCodes: 'All'
          } }
          activeProps={ { className: 'bold' } }
          preload="intent"
        >
          Metro Train Status
        </Link>
      </div>
      <hr />
      <Outlet />
      <TanStackRouterDevtools />
    </>
  ),
  pendingComponent: () => 'Loading...'
});
