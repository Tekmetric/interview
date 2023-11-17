import { lazy, Suspense, useEffect, useState } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { ComponentFactory } from './components';

const GameList = lazy(() => ComponentFactory.GameList());
const Pokedex = lazy(() => ComponentFactory.Pokedex());

const App = () => {
  const [games, setGames] = useState([]);

  useEffect(() => {
    const getGames = async () => {
      const response = await fetch('https://pokeapi.co/api/v2/version-group?limit=30').then(
        (data) => data.json()
      );
      setGames(response.results.reverse());
    };

    if (!games || games.length === 0) getGames().catch(console.error);
  }, []);

  return (
    <Suspense>
      <Routes>
        <Route path={'/'} element={<GameList games={games} />} />
        <Route path={'/pokedex'}>
          <Route index element={<Navigate to={'/'} replace />} />
          <Route path={'/pokedex/:id'} element={<Pokedex />} />
        </Route>
      </Routes>
    </Suspense>
  );
};

export default App;
