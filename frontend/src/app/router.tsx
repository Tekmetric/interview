import { createBrowserRouter, Navigate } from 'react-router';

import { Layout } from '../components/Layout';
import { NotFoundPage } from '../components/NotFoundPage';

// Pages load through the router's native `lazy` so each route becomes its own
// chunk: the initial bundle carries only the shell, and a page's code is
// fetched when navigation to it starts (no render-then-fetch waterfall).
// The Layout stays static — it is needed on every page.
export const routes = [
  {
    path: '/',
    element: <Layout />,
    children: [
      { index: true, element: <Navigate to="/characters" replace /> },
      {
        path: 'characters',
        lazy: async () => ({
          Component: (await import('../features/characters/CharactersPage')).CharactersPage,
        }),
      },
      {
        path: 'characters/:characterId',
        lazy: async () => ({
          Component: (await import('../features/characters/CharacterDetailPage'))
            .CharacterDetailPage,
        }),
      },
      {
        path: 'episodes',
        lazy: async () => ({
          Component: (await import('../features/episodes/EpisodesPage')).EpisodesPage,
        }),
      },
      {
        path: 'episodes/:episodeId',
        lazy: async () => ({
          Component: (await import('../features/episodes/EpisodeDetailPage')).EpisodeDetailPage,
        }),
      },
      {
        path: 'locations',
        lazy: async () => ({
          Component: (await import('../features/locations/LocationsPage')).LocationsPage,
        }),
      },
      {
        path: 'locations/:locationId',
        lazy: async () => ({
          Component: (await import('../features/locations/LocationDetailPage')).LocationDetailPage,
        }),
      },
      {
        path: 'favorites',
        lazy: async () => ({
          Component: (await import('../features/favorites/FavoritesPage')).FavoritesPage,
        }),
      },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
];

export const router = createBrowserRouter(routes);
