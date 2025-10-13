import { Outlet, createRootRouteWithContext } from '@tanstack/react-router';
import { TanStackRouterDevtoolsPanel } from '@tanstack/react-router-devtools';
import { ReactQueryDevtoolsPanel } from '@tanstack/react-query-devtools'
import { TanStackDevtools } from '@tanstack/react-devtools';
import MainNavigation from '@/components/MainNavigation';
import type { QueryClient } from '@tanstack/react-query';

interface RouterContext {
  queryClient: QueryClient;

}

export const Route = createRootRouteWithContext<RouterContext>()({
  component: () => (
    <>
      <MainNavigation />
      <Outlet />
      {!import.meta.env.PROD && (<TanStackDevtools
        config={{
          position: 'bottom-right',
        }}
        plugins={[
          {
            name: 'Tanstack Router',
            render: <TanStackRouterDevtoolsPanel />,
          },
          {
            name: 'React Query',
            render: <ReactQueryDevtoolsPanel />,
          },
        ]}
      />)}
    </>
  ),
});
