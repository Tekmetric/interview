import { lazy, Suspense, useEffect, useState } from 'react';
import { Route, Routes } from 'react-router-dom';
import { ComponentFactory } from './components';

const GameList = lazy(() => ComponentFactory.GameList());

const App = () => {
  const [games, setGames] = useState([])

  useEffect(() => {
    const getGames = async () => {
      const response = await fetch('https://pokeapi.co/api/v2/version-group?limit=30').then(data => data.json());
      setGames(response.results);
    }

    if (!games || games.length === 0) getGames().catch(console.error);
  }, [])

  return (
    <main className="App">
      <div>
        <Suspense>
          <Routes>
            <Route path={"/"} element={<GameList games={games} />} />
          </Routes>
        </Suspense>
      </div>
    </main>
  );
};

export default App;
