import React, {Suspense, useEffect, useState} from 'react'
import {classNames} from "../../utils/Utils";
import styles from './Pokedex.module.scss';
import {ComponentFactory} from "../../components";
import { Pokedex as P } from "../../models";
import {Simulate} from "react-dom/test-utils";
export interface PokedexProps {};

const Card = React.lazy(() => ComponentFactory.CardAsync());

const Pokedex = () => {
    const [pokedex, setPokedex] = useState<P[]>();

    const fetchPokemon = async () => {
        const response = await fetch('https://pokeapi.co/api/v2/pokemon?').then(data => data.json());
        setPokedex(response.results);
    }

    useEffect(() => {
        fetchPokemon().catch(console.error);
    }, []);

    return (
        <div className={classNames(styles.container)}>
            <Suspense>
                {pokedex?.map((p: P) => (
                    <Card key={p.url} url={p.url} />
                ))}
            </Suspense>
        </div>
    )
};

export default Pokedex;
