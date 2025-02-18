import type { QueryClient } from '@tanstack/react-query';
import { NavBar } from '@components/NavBar';
import { StationSelectKBar } from '@components/StationSelectKBar';
import { ThemeProvider } from '@components/ui/theme-provider';
import { createRootRouteWithContext, Outlet } from '@tanstack/react-router';
import { lazy } from 'react';

// Only load the dev tools outside of building for production
const TanStackRouterDevtools
  = import.meta.env.PROD
    ? () => null
    : lazy(() =>
        import('@tanstack/router-devtools').then(res => ({
          default: res.TanStackRouterDevtools
        }))
      );

export const Route = createRootRouteWithContext<{ queryClient: QueryClient }>()({
  wrapInSuspense: true,
  component: RootRoute,
  pendingComponent: () => 'Loading...'
});

function RootRoute() {
  return (
    <ThemeProvider defaultTheme="light" storageKey="vite-ui-theme">
      <StationSelectKBar />
      <NavBar />
      <Outlet />
      <TanStackRouterDevtools />
    </ThemeProvider>
  );
}
