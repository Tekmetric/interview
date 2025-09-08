import React, { useEffect } from "react";
import Sky from "./components/sky";
import Loader from "./components/loader";
import Rockets from "./components/rockets-list";
import { RocketsProvider, useRocketsContext } from "./contexts/RocketsContext";

function AppContent() {
  const { rockets, maxRocketDimensions, loading, error, fetchRockets } =
    useRocketsContext();

  useEffect(() => {
    fetchRockets();
  }, []);

  return (
    <div className="App">
      <Sky />
      {loading && <Loader text="Fetching Rockets..." />}
      {error && <div>{error}</div>}
      {maxRocketDimensions !== undefined && <Rockets rockets={rockets} />}
    </div>
  );
}

function App() {
  return (
    <RocketsProvider>
      <AppContent />
    </RocketsProvider>
  );
}

export default App;
