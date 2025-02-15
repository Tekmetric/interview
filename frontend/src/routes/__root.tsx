import type { QueryClient } from '@tanstack/react-query';
import { NavBar } from '@components/NavBar';
import { StationSelect } from '@components/StationSelect';
import { ThemeProvider } from '@components/ui/theme-provider';
import { useKBarShortcut } from '@hooks/useKBarShortcut';
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
  const [showStationSelect, setShowStationSelect] = useKBarShortcut();

  return (
    <ThemeProvider defaultTheme="light" storageKey="vite-ui-theme">
      { showStationSelect && (
        <div className="absolute mx-auto my-auto w-full h-full bg-gray-500/80 z-10000">
          <div className="flex justify-center">
            <div className="absolute top-1/3 w-[60%]">
              <StationSelect
                className="text-3xl"
                onSelect={ () => setShowStationSelect(false) }
              />
            </div>
          </div>
        </div>
      )}
      <NavBar />
      <Outlet />
      <TanStackRouterDevtools />
    </ThemeProvider>
  );
}
