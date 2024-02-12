import React, { useState } from 'react';
import ListView from './pages/ListView';
import DetailView from './pages/DetailView';
import ProvidePersons from './data/PersonsProvider';

const App = () => {
  const [id, setId] = useState();

  return (
    <ProvidePersons>
      <div className="App">
        <header className="App-header">
          {!id && <ListView navigate={setId} />}
          {id && <DetailView id={id} prev={() => setId(undefined)} />}
        </header>
      </div>
    </ProvidePersons>
  );
}

export default App;
