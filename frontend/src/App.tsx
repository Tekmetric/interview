import { useState } from 'react';
import { BqButton, BqCard, BqIcon } from '@beeq/react';
import ViteSVG from '/vite.svg';
import ReactSVG from './assets/react.svg';

import './App.css';

function App() {
  const [count, setCount] = useState(0);

  return (
    <BqCard>
      <h1>BEEQ</h1>
      <h2>
        with React <img src={ReactSVG} className="logo react animate-logo-spin" alt="React logo" /> + Vite{' '}
        <img src={ViteSVG} className="logo" alt="Vite logo" />
      </h2>
      <h3>On CodeSandbox!</h3>
      <div className="m-bs-m">
        <BqButton onBqClick={() => setCount((count) => count + 1)}>
          Give a thumbs up
          <BqIcon name="thumbs-up" slot="suffix" />
        </BqButton>

        <p className="m-bs-m m-be-m">Total of thumbs: {count}</p>

        <p className="m-bs-m m-be-m">
          Edit <code>src/App.tsx</code> and save to test HMR.
        </p>
      </div>
    </BqCard>
  );
}

export default App;
