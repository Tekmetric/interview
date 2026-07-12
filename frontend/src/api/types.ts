// Types mirror https://rickandmortyapi.com/documentation responses 1:1.
// Field names stay snake_case where the API uses it (air_date) — renaming at
// the boundary would buy consistency at the cost of a mapping layer.

export interface Info {
  count: number;
  pages: number;
  next: string | null;
  prev: string | null;
}

export interface Paginated<T> {
  info: Info;
  results: T[];
}

export type CharacterStatus = 'Alive' | 'Dead' | 'unknown';
export type CharacterGender = 'Female' | 'Male' | 'Genderless' | 'unknown';

// Cross-link to another resource: name plus its API URL (may be empty when unknown).
export interface ResourceRef {
  name: string;
  url: string;
}

export interface Character {
  id: number;
  name: string;
  status: CharacterStatus;
  species: string;
  type: string;
  gender: CharacterGender;
  origin: ResourceRef;
  location: ResourceRef;
  image: string;
  episode: string[];
  url: string;
  created: string;
}

export interface Episode {
  id: number;
  name: string;
  air_date: string;
  episode: string;
  characters: string[];
  url: string;
  created: string;
}

export interface Location {
  id: number;
  name: string;
  type: string;
  dimension: string;
  residents: string[];
  url: string;
  created: string;
}
