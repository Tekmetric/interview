import React from 'react';
import './index.css';
import { MovieSearch } from './components/MovieSearch/MovieSearch';
import { WatchmodeProvider } from './providers/WatchmodeContextProvider/WatchmodeProvider';
 
const App = () => {
  return (
    <div className="App">
      <WatchmodeProvider>
        <MovieSearch />
      </WatchmodeProvider>
    </div>
  );
}

export default App;
