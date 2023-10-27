import React, {Suspense, useEffect, useState} from 'react';
import {Pokemon as P} from "../../models";
import {Link, useParams} from "react-router-dom";
import {classNames} from "../../utils/Utils";
import styles from './Pokemon.module.scss';
import {ComponentFactory} from "../../components";

const Type = React.lazy(() => ComponentFactory.TypeAsync());

export interface PokemonProps {
  pokemon?: P;
};

const Pokemon = (props: PokemonProps) => {
  const {pokemon} = props;
  const [pokemonDetails, setPokemonDetails] = useState<P | undefined>(pokemon);
  const {id} = useParams();

  const fetchPokemon = async () => {
    const response = await fetch(`https://pokeapi.co/api/v2/pokemon/${id}`).then(data => data.json());
    setPokemonDetails(response);
  };

  useEffect(() => {
    if (!pokemon && id) {
      fetchPokemon().catch(console.error);
    }
  }, []);

  return (
      <div className={classNames(styles.container)}>
        <Link to={'/pokemon'} className={classNames(styles.backLink)}>
          Back to Pokedex
        </Link>
        {pokemonDetails ? (
          <div className={classNames(styles.heroContent)}>
            <img className={styles.image} src={pokemonDetails.sprites.other.dream_world.front_default} alt={`${pokemon?.name}`} />
            <div className={classNames(styles.content)}>
              <div className={classNames(styles.types)}>
                <Suspense>
                  {pokemonDetails.types.map(t => (
                      <Type name={t.type.name} />
                  ))}
                </Suspense>
              </div>
              <h1 className={styles.name}>{pokemonDetails.name}</h1>
              <div className={classNames(styles.statsContainer)}>
                {pokemonDetails.stats.map(stat => (
                    <div className={classNames(styles.stat)}>
                      <p className={classNames(styles.label)}>{stat.stat.name}</p>
                      <span className={classNames(styles.amount)}>{stat.base_stat}</span>
                    </div>
                ))}
              </div>
            </div>
          </div>
          ) : (
              <p>No Pokemon found.</p>
          )}
      </div>)
};

export default Pokemon;
