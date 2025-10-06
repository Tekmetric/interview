export type PokemonTypeName = 
  | 'normal' | 'fire' | 'water' | 'electric' | 'grass' | 'ice' | 'fighting'
  | 'poison' | 'ground' | 'flying' | 'psychic' | 'bug' | 'rock' | 'ghost'
  | 'dragon' | 'dark' | 'steel' | 'fairy';

export interface PokemonType {
  type: {
    name: PokemonTypeName;
  };
}

export interface PokemonStat {
  stat: {
    name: string;
  };
  base_stat: number;
}

export interface PokemonSprites {
  front_default: string | null;
}

export interface Pokemon {
  id: number;
  name: string;
  height: number;
  weight: number;
  sprites: PokemonSprites;
  types: PokemonType[];
  stats: PokemonStat[];
}

/**
 * Raw PokeAPI response type
 * Used for API responses that may have optional/missing fields
 */
export interface PokeApiPokemonResponse {
  id: number;
  name: string;
  height?: number;
  weight?: number;
  sprites?: {
    front_default?: string | null;
  };
  types?: Array<{
    type: {
      name: string;
    };
  }>;
  stats?: Array<{
    stat: {
      name: string;
    };
    base_stat: number;
  }>;
}

export interface ApiHealthStatus {
  status: 'healthy' | 'unhealthy';
  message: string;
  timestamp: string;
}
