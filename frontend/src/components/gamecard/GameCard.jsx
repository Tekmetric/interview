import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const GameCard = ({ name, url }) => {
  const [pokedexUrl, setPokedexUrl] = useState();

  useEffect(() => {
    const getVersionGroup = async () => {
      const response = await fetch(url).then(data => data.json());
      setPokedexUrl(response.pokedexes);
    }
    
    if (url && !pokedexUrl) {
      getVersionGroup().catch(console.error);
    }
  }, []);

  const Games = {
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
    'colosseum': 'Colosseum',
    'xd': 'XD: Gale of Darkness',
    'black-2-white-2': 'Black 2 & White 2',
    'x-y': 'X & Y',
    'omega-ruby-alpha-sapphire': 'Omega Ruby & Alpha Sapphire',
    'sun-moon': 'Sun & Moon',
    'ultra-sun-ultra-moon': 'Ultra Sun & Ultra Moon',
    'lets-go-pikachu-lets-go-eevee': "Let's Go Pikachu & Let's Go Eevee",
    'sword-shield': 'Sword & Shield',
    'the-isle-of-armor': 'The Isle of Armor',
    'the-crown-tundra': 'The Crown Tundra',
    'brilliant-diamond-and-shining-pearl': 'Brilliant Diamond & Shining Pearl',
    'legends-arceus': 'Legends: Arceus',
    'scarlet-violet': 'Scarlet & Violet',
    'the-teal-mask': 'The Teal Mask',
    'the-indigo-disk': 'The Indigo Disk'
  }

  return <Link to={`/${name}/`} state={{ from: pokedexUrl}} >{Games[name] || name}</Link>;
};

export default GameCard;
