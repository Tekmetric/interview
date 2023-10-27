import React, {useEffect, useState} from 'react';
import {Link} from "react-router-dom";
import {classNames} from "../../utils/Utils";
import styles from './Card.module.scss';
import {Pokemon} from "../../models";
export interface CardProps {
    url: string;
};

const Card = (props: CardProps) => {
    const {url} = props;
    const [pokemon, setPokemon] = useState<Pokemon>();
    const fetchPokemonDetails = async () => {
      const response = await fetch(url).then(data => data.json());
      setPokemon(response);

    };
    useEffect(() => {
        fetchPokemonDetails();
    }, []);

    return pokemon ? (
        <Link to={`/pokemon/${pokemon.id}`} className={classNames(styles.container)}>
            <img className={classNames(styles.image)} src={pokemon.sprites.other.dream_world.front_default} alt={`${pokemon.name}`} />
            <div className={classNames(styles.content)}>
                <p className={classNames(styles.name)}>
                    {pokemon.name}
                </p>
            </div>
        </Link>
    ) : null;
};

export default Card;
