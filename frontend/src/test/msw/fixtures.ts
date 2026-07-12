import type { Character, Episode, Info, Location, Paginated } from '../../api/types';

const API = 'https://rickandmortyapi.com/api';

export function makeCharacter(id: number, overrides: Partial<Character> = {}): Character {
  return {
    id,
    name: `Citizen ${id}`,
    status: 'Alive',
    species: 'Human',
    type: '',
    gender: 'Male',
    origin: { name: 'Earth (C-137)', url: `${API}/location/1` },
    location: { name: 'Citadel of Ricks', url: `${API}/location/3` },
    image: `${API}/character/avatar/${id}.jpeg`,
    episode: [`${API}/episode/1`, `${API}/episode/2`],
    url: `${API}/character/${id}`,
    created: '2017-11-04T18:48:46.250Z',
    ...overrides,
  };
}

export function makeEpisode(id: number, overrides: Partial<Episode> = {}): Episode {
  return {
    id,
    name: `Episode ${id}`,
    air_date: 'December 2, 2013',
    episode: `S01E${String(id).padStart(2, '0')}`,
    characters: [`${API}/character/1`],
    url: `${API}/episode/${id}`,
    created: '2017-11-10T12:56:33.798Z',
    ...overrides,
  };
}

export function makeLocation(id: number, overrides: Partial<Location> = {}): Location {
  return {
    id,
    name: `Dimension ${id}`,
    type: 'Planet',
    dimension: `Dimension C-${id}`,
    residents: [`${API}/character/1`],
    url: `${API}/location/${id}`,
    created: '2017-11-10T12:42:04.162Z',
    ...overrides,
  };
}

// A stable default universe: 25 characters => 2 pages of 20 + 5.
export const fixtureCharacters: Character[] = [
  makeCharacter(1, { name: 'Rick Sanchez', status: 'Alive', gender: 'Male' }),
  makeCharacter(2, { name: 'Morty Smith', status: 'Alive', gender: 'Male' }),
  makeCharacter(3, { name: 'Summer Smith', status: 'Alive', gender: 'Female' }),
  makeCharacter(4, { name: 'Birdperson', status: 'Dead', species: 'Bird-Person' }),
  ...Array.from({ length: 21 }, (_, index) => makeCharacter(index + 5)),
];

export const fixtureEpisodes: Episode[] = [
  makeEpisode(1, { name: 'Pilot', episode: 'S01E01' }),
  makeEpisode(2, { name: 'Lawnmower Dog', episode: 'S01E02' }),
  makeEpisode(3, { name: 'A Rickle in Time', episode: 'S02E01' }),
];

export const fixtureLocations: Location[] = [
  makeLocation(1, { name: 'Earth (C-137)' }),
  makeLocation(3, { name: 'Citadel of Ricks', type: 'Space station' }),
];

export const PAGE_SIZE = 20;

export function paginate<T>(items: T[], page: number, url: string): Paginated<T> {
  const pages = Math.max(1, Math.ceil(items.length / PAGE_SIZE));
  const results = items.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);
  const info: Info = {
    count: items.length,
    pages,
    next: page < pages ? `${url}?page=${page + 1}` : null,
    prev: page > 1 ? `${url}?page=${page - 1}` : null,
  };
  return { info, results };
}
