import React, { useState } from 'react';
import Column from './components/Column';

const App = () => {
  const [data, setData] = useState<Array<any>>([]);

  return (
    <div className="App">
      <header className="App-header">
        <h2>Welcome to the interview app!</h2>
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
      </header>
      <Column />
    </div>
  );
};

export default App;
