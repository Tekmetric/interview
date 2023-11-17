import { lazy, Suspense, /* useEffect, useState */ } from 'react';
import { ComponentFactory } from "..";

const GameCard = lazy(() => ComponentFactory.GameCard());

const GameList = ({ games }) => {
  // const [list, setList] = useState();

  // useEffect(() => {
  //   const getVersionGroup = async () => {
  //     const response = await fetch(url).then(data => data.json());
  //     console.log(name, response.pokedexes[0]);

  //     setPokedexUrl(response.pokedexes);
  //   }
    
  //   if (url && !pokedexUrl) {
  //     getVersionGroup().catch(console.error);
  //   }
  // }, []);

  return (
    <div>
      <ul>
        <Suspense>
        {games?.map((g, i) => (
          <li key={i}>
            <GameCard name={g.name} url={g.url} />
          </li>
        ))}
        </Suspense>
      </ul>
    </div>
  );
};

export default GameList;
