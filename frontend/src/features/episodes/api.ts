import { rickAndMortyApi } from '../../api/rickAndMortyApi';
import type { Episode, Paginated } from '../../api/types';
import { normalizeToArray } from '../../utils/normalizeToArray';

export const episodesApi = rickAndMortyApi.injectEndpoints({
  endpoints: (build) => ({
    // The show has 51 episodes (3 pages) — small and finite, so unlike the
    // 826-character list this fetches everything once and lets the UI group
    // and search entirely client-side, with no request per keystroke.
    getAllEpisodes: build.query<Episode[], void>({
      queryFn: async (_arg, _api, _extraOptions, fetchWithBQ) => {
        const episodes: Episode[] = [];
        let page = 1;
        let totalPages = 1;
        do {
          const result = await fetchWithBQ(`/episode?page=${page}`);
          if (result.error) {
            return { error: result.error };
          }
          const data = result.data as Paginated<Episode>;
          episodes.push(...data.results);
          totalPages = data.info.pages;
          page += 1;
        } while (page <= totalPages);
        return { data: episodes };
      },
    }),

    getEpisode: build.query<Episode, number>({
      query: (id) => `/episode/${id}`,
    }),

    // Batch fetch for detail pages: /episode/1,2,3 in a single request.
    getEpisodesByIds: build.query<Episode[], number[]>({
      query: (ids) => `/episode/${ids.join(',')}`,
      transformResponse: (response: Episode | Episode[]) => normalizeToArray(response),
    }),
  }),
});

export const { useGetAllEpisodesQuery, useGetEpisodeQuery, useGetEpisodesByIdsQuery } = episodesApi;
