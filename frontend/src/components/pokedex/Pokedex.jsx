import { lazy, useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { ComponentFactory } from '..';
import styles from './Pokedex.module.scss';

const PokedexEntry = lazy(() => ComponentFactory.PokedexEntry());

const Pokedex = () => {
  const location = useLocation();

  const [dex, setDex] = useState();

  useEffect(() => {
    const getPokedex = async (url) => {
      const response = await fetch(url).then((data) => data.json());
      setDex(response);
    };

    if (location.state.from || location.state.from.length !== 0) {
      const { url } = location.state.from[0];
      getPokedex(url);
    }
  }, []);

  return (
    <div className={styles.container}>
      <div className={styles.menu}>
        <Link to={'/'}>Back to Game Selection</Link>
      </div>
      {dex?.pokemon_entries && (
        <ol className={styles.list}>
          {dex.pokemon_entries.map((e, i) => (
            <li key={i}>
              <PokedexEntry entry={e} />
            </li>
          ))}
        </ol>
      )}
    </div>
  );
};

export default Pokedex;
