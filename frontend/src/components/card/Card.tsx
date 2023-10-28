import React, {Suspense, useEffect, useState} from 'react';
import {Link} from "react-router-dom";
import {classNames} from "../../utils/Utils";
import styles from './Card.module.scss';
import {Pokemon} from "../../models";
import {ComponentFactory, InView} from "../index";

const Type = React.lazy(() => ComponentFactory.TypeAsync());

export interface CardProps {
    /**
     * URL of the Pokemon from the Pokedex dataset
     */
    url: string;
};

/**
 * Card component is used on the Pokedex page to render for each Pokemon loaded.
 * @param props
 * @constructor
 */
const Card = (props: CardProps) => {
    const {url} = props;
    const [pokemon, setPokemon] = useState<Pokemon>();
    const [ inView, setInView ] = useState<boolean>(false);

    useEffect(() => {
        const fetchPokemonDetails = async () => {
            const response = await fetch(url).then(data => data.json());
            setPokemon(response);
        };

        if (inView) {
            fetchPokemonDetails().catch(console.error);
        }
    }, [inView, url]);

    return (
        <InView onChange={setInView} className={classNames(styles.container)}>
            {pokemon ? (
                <Link to={`/pokemon/${pokemon?.id}`}>
                    <img className={classNames(styles.image)} src={pokemon?.sprites.other.dream_world.front_default ?? ''} alt={`${pokemon.name}`} />
                    <div className={classNames(styles.content)}>
                        <p className={classNames(styles.name)}>
                            {pokemon.name}
                        </p>
                        <div className={classNames(styles.types)}>
                            <Suspense>
                                {pokemon.types.map(t => (
                                    <Type key={`${pokemon.name}-${t.type.name}`} name={t.type.name}/>
                                ))}
                            </Suspense>
                        </div>
                    </div>
                </Link> ) : null}
        </InView>
    )
};

export default Card;
