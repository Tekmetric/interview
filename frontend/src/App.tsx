import React, { useState } from 'react';
import Column from './components/Column';

const App = () => {
  // const [data, setData] = useState<Array<any>>([]);

  return (
    <div className="App">
      <header className="App-header">
        <h1 className="text-3xl font-bold text-green-500 underline">
          React Interview
        </h1>

        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
      </header>
      <Column />
    </div>
  );
};

export default App;
