import { lazy, Suspense, useEffect, useState } from 'react';
import { prefixGameTitle } from '../../utils/webhelper';
import { ComponentFactory } from '..';
import styles from './GameList.module.scss';

const GameCard = lazy(() => ComponentFactory.GameCard());

export const AllowedGames = {
  'red-blue': 'Red & Blue',
  'yellow': 'Yellow',
  'gold-silver': 'Gold & Silver',
  'crystal': 'Crystal',
  'ruby-sapphire': 'Ruby & Sapphire',
  'emerald': 'Emerald',
  'firered-leafgreen': 'FireRed & LeafGreen',
  'diamond-pearl': 'Diamond & Pearl',
  'platinum': 'Platinum',
  'heartgold-soulsilver': 'HeartGold & SoulSilver',
  'black-white': 'Black & White',
  'black-2-white-2': 'Black 2 & White 2',
  'x-y': 'X & Y',
  'omega-ruby-alpha-sapphire': 'Omega Ruby & Alpha Sapphire',
  'sun-moon': 'Sun & Moon',
  'ultra-sun-ultra-moon': 'Ultra Sun & Ultra Moon',
  'lets-go-pikachu-lets-go-eevee': "Let's Go Pikachu & Eevee",
  'sword-shield': 'Sword & Shield',
  'brilliant-diamond-and-shining-pearl': 'Brilliant Diamond & Shining Pearl',
  'legends-arceus': 'Legends: Arceus',
  'scarlet-violet': 'Scarlet & Violet',
};

const GameList = ({ games }) => {
  const [list, setList] = useState();

  useEffect(() => {
    if (games) {
      setList(games.filter((g) => AllowedGames[g.name]));
    }
  }, [games]);

  return (
    <div className={styles.container}>
      <header className={styles.header}>Select a Game</header>
      <div className={styles.list}>
        <Suspense>
          {list?.map((g, i) => (
            <GameCard
              key={i}
              name={prefixGameTitle(AllowedGames[g.name])}
              slug={g.name}
              url={g.url}
            />
          ))}
        </Suspense>
      </div>
    </div>
  );
};

export default GameList;
