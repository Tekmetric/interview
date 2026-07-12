import type { Character, Episode, Location } from '../api/types';
import { paginate } from './msw/fixtures';

export interface ApiPool {
  characters: Character[];
  episodes: Episode[];
  locations: Location[];
}

export interface SimResponse {
  status: number;
  body: unknown;
}

const NOT_FOUND: SimResponse = { status: 404, body: { error: 'There is nothing here' } };

function filterCharacters(pool: Character[], url: URL): Character[] {
  const name = url.searchParams.get('name')?.toLowerCase();
  const status = url.searchParams.get('status')?.toLowerCase();
  const gender = url.searchParams.get('gender')?.toLowerCase();
  return pool.filter(
    (character) =>
      (!name || character.name.toLowerCase().includes(name)) &&
      (!status || character.status.toLowerCase() === status) &&
      (!gender || character.gender.toLowerCase() === gender),
  );
}

function listResponse<T>(items: T[], url: URL, listUrl: string): SimResponse {
  if (items.length === 0) {
    return NOT_FOUND;
  }
  const page = Number(url.searchParams.get('page') ?? '1');
  return { status: 200, body: paginate(items, page, listUrl) };
}

function byIds<T extends { id: number }>(pool: T[], idsPart: string): SimResponse {
  const ids = idsPart.split(',').map(Number);
  const found = pool.filter((item) => ids.includes(item.id));
  if (found.length === 0) {
    return NOT_FOUND;
  }
  // Single id => bare object, like the real API.
  return { status: 200, body: ids.length === 1 ? found[0] : found };
}

// One faithful in-memory copy of the Rick and Morty API contract — including
// its quirks (404 for "no matches", bare object for single-id batches).
// MSW (unit tests) and Playwright routes (e2e) both answer through this, so
// the two suites can never drift apart in what they believe the API does.
export function simulateApi(pool: ApiPool, url: URL): SimResponse {
  // Pathname shape: /api/<resource>[/<ids>]
  const [, , resource, idsPart] = url.pathname.split('/');

  switch (resource) {
    case 'character':
      return idsPart
        ? byIds(pool.characters, idsPart)
        : listResponse(filterCharacters(pool.characters, url), url, `${url.origin}/api/character`);
    case 'episode':
      return idsPart
        ? byIds(pool.episodes, idsPart)
        : listResponse(pool.episodes, url, `${url.origin}/api/episode`);
    case 'location': {
      if (idsPart) {
        return byIds(pool.locations, idsPart);
      }
      const name = url.searchParams.get('name')?.toLowerCase();
      const matches = pool.locations.filter(
        (location) => !name || location.name.toLowerCase().includes(name),
      );
      return listResponse(matches, url, `${url.origin}/api/location`);
    }
    default:
      return NOT_FOUND;
  }
}
