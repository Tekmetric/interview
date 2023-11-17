import { useEffect, useState } from 'react';
import styles from './PokedexEntry.module.scss';

const PokedexEntry = ({ entry }) => {
  const [pokemon, setPokemon] = useState();

  useEffect(() => {
    const getEntryDetails = async (url) => {
      const response = await fetch(url).then((data) => data.json());
      setPokemon(response);
    };

    if (entry && !pokemon) getEntryDetails(entry.pokemon_species.url);
  }, [entry]);

  // useEffect(() => {
  //   if (pokemon) console.log(pokemon);
  // }, [pokemon]);

  return (
    <div className={styles.box}>
      {pokemon ? (
        <img
          src={`https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemon.id}.png`}
          alt={`official sprite for ${pokemon.name}`}
        />
      ) : (
        <span>Loading..</span>
      )}
    </div>
  );
};

export default PokedexEntry;
