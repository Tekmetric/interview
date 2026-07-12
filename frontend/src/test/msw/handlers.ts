import { http, HttpResponse, type JsonBodyType } from 'msw';

import { simulateApi } from '../apiSim';
import { fixtureCharacters, fixtureEpisodes, fixtureLocations } from './fixtures';

const pool = {
  characters: fixtureCharacters,
  episodes: fixtureEpisodes,
  locations: fixtureLocations,
};

// Every request is answered by the shared API simulator (see ../apiSim.ts).
const respond = ({ request }: { request: Request }) => {
  const { status, body } = simulateApi(pool, new URL(request.url));
  return HttpResponse.json(body as JsonBodyType, { status });
};

export const handlers = [
  http.get('https://rickandmortyapi.com/api/character', respond),
  http.get('https://rickandmortyapi.com/api/character/:ids', respond),
  http.get('https://rickandmortyapi.com/api/episode', respond),
  http.get('https://rickandmortyapi.com/api/episode/:ids', respond),
  http.get('https://rickandmortyapi.com/api/location', respond),
  http.get('https://rickandmortyapi.com/api/location/:ids', respond),
];
