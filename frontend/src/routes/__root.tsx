import type { QueryClient } from '@tanstack/react-query';
import { NavBar } from '@components/NavBar';
import { StationSelect } from '@components/StationSelect';
import { useKey, useKeyCombo } from '@rwh/react-keystrokes';
import { createRootRouteWithContext, Outlet } from '@tanstack/react-router';
import { TanStackRouterDevtools } from '@tanstack/router-devtools';
import { useEffect, useState } from 'react';

export const Route = createRootRouteWithContext<{ queryClient: QueryClient }>()({
  wrapInSuspense: true,
  component: RootRoute,
  pendingComponent: () => 'Loading...'
});

function RootRoute() {
  const isMetaComboPressed = useKeyCombo('meta + k');
  const isControlComboPressed = useKeyCombo('control + k');
  const isEscapePressed = useKey('escape');
  const [showStationSelect, setShowStationSelect] = useState(false);

  useEffect(() => {
    if (isMetaComboPressed || isControlComboPressed) {
      setShowStationSelect(true);
    }
  }, [isMetaComboPressed, isControlComboPressed]);

  useEffect(() => {
    if (isEscapePressed) {
      setShowStationSelect(false);
    }
  }, [isEscapePressed]);

  return (
    <>
      { showStationSelect && (
        <div className="absolute mx-auto my-auto w-full h-full bg-gray-500/80 z-10000">
          <div className="absolute top-1/3 w-full px-4">
            <StationSelect
              className="text-3xl"
              onSelect={ () => setShowStationSelect(false) }
            />
          </div>
        </div>
      )}
      <NavBar />
      <hr />
      <Outlet />
      <TanStackRouterDevtools />
    </>
  );
}
