import React from "react";
import Sky from "./components/sky";
import Loader from "./components/loader";
import { useRockets } from "./hooks/useRockets";

function App() {
  const { loading, error } = useRockets();

  return (
    <div className="App">
      <Sky />
      {loading && <Loader text="Fetching Rockets..." />}
      {error && <div>{error}</div>}
    </div>
  );
}

export default App;
