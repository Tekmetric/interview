import React, {Suspense, useEffect, useState} from 'react'
import {classNames} from "../../utils/Utils";
import styles from './Pokedex.module.scss';
import {ComponentFactory} from "../../components";
import { Pokedex as P } from "../../models";

const Card = React.lazy(() => ComponentFactory.CardAsync());

export interface PokedexProps {};

/**
 * Pokedex is the main list page of all Pokemon.
 * @constructor
 */
const Pokedex = () => {
    const [pokedex, setPokedex] = useState<P[]>();
    const pageSize = process.env.REACT_APP_PAGE_SIZE ?? 20;

    const fetchPokemon = async () => {
        const response = await fetch(`https://pokeapi.co/api/v2/pokemon?limit=${pageSize}`).then(data => data.json());
        setPokedex(response.results);
    }

    useEffect(() => {
        fetchPokemon().catch(console.error);
    }, []);

    return (
        <div className={classNames(styles.container)}>
            <Suspense>
                {pokedex?.map((p: P) => (
                    <Card key={p.name} url={p.url} />
                ))}
            </Suspense>
        </div>
    )
};

export default Pokedex;
