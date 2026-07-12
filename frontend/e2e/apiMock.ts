import type { Page } from '@playwright/test';

import { simulateApi, type ApiPool } from '../src/test/apiSim';
import { makeCharacter, makeEpisode, makeLocation } from '../src/test/msw/fixtures';

const API_ORIGIN = 'https://rickandmortyapi.com';

// A larger universe than the unit-test fixtures: enough characters that
// pagination and virtualization behave like production (300 => 15 pages).
const characters = [
  makeCharacter(1, { name: 'Rick Sanchez' }),
  makeCharacter(2, { name: 'Morty Smith' }),
  ...Array.from({ length: 44 }, (_, index) => makeCharacter(index + 3)),
  makeCharacter(47, {
    name: 'Birdperson',
    species: 'Bird-Person',
    episode: [`${API_ORIGIN}/api/episode/11`],
  }),
  ...Array.from({ length: 253 }, (_, index) => makeCharacter(index + 48)),
];

const episodes = [
  ...Array.from({ length: 10 }, (_, index) => makeEpisode(index + 1)),
  makeEpisode(11, {
    name: 'Ricksy Business',
    episode: 'S01E11',
    characters: [`${API_ORIGIN}/api/character/47`],
  }),
  ...Array.from({ length: 40 }, (_, index) =>
    makeEpisode(index + 12, {
      episode: `S0${Math.floor((index + 11) / 10) + 1}E${String(((index + 11) % 10) + 1).padStart(2, '0')}`,
    }),
  ),
];

const locations = [
  makeLocation(1, { name: 'Earth (C-137)' }),
  makeLocation(3, { name: 'Citadel of Ricks', type: 'Space station' }),
];

const pool: ApiPool = { characters, episodes, locations };

// Answers every Rick and Morty API request from the shared simulator:
// deterministic data, no network flakiness, no rate limits — and the exact
// same contract quirks the unit-test mocks encode.
export async function mockApi(page: Page) {
  await page.route(`${API_ORIGIN}/api/**`, async (route) => {
    const { status, body } = simulateApi(pool, new URL(route.request().url()));
    await route.fulfill({ status, json: body });
  });
  // Avatar images: a 1x1 transparent PNG keeps layout without network fetches.
  await page.route(`${API_ORIGIN}/api/character/avatar/**`, (route) =>
    route.fulfill({
      status: 200,
      contentType: 'image/png',
      body: Buffer.from(
        'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+P+/HgAFhAJ/wlseKgAAAABJRU5ErkJggg==',
        'base64',
      ),
    }),
  );
}
