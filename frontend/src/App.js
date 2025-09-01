import React from "react";
import Sky from "./components/sky";
import Loader from "./components/loader";
import Rockets from "./components/rockets-list";
import { useRockets } from "./hooks/useRockets";

function App() {
  const { rockets, loading, error } = useRockets();

  return (
    <div className="App">
      <Sky />
      {loading && <Loader text="Fetching Rockets..." />}
      {error && <div>{error}</div>}
      {rockets && <Rockets rockets={rockets} />}
    </div>
  );
}

export default App;
