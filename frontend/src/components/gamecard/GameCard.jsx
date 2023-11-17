import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import styles from './GameCard.module.scss';

const GameCard = ({ name, slug, url }) => {
  const [pokedexUrl, setPokedexUrl] = useState();

  useEffect(() => {
    const getVersionGroup = async () => {
      const response = await fetch(url).then((data) => data.json());
      setPokedexUrl(response.pokedexes);
    };

    if (url && !pokedexUrl) {
      getVersionGroup().catch(console.error);
    }
  }, []);

  return (
    <Link to={`/pokedex/${slug}/`} state={{ from: pokedexUrl }} className={styles.card}>
      <span>{name}</span>
    </Link>
  );
};

export default GameCard;
