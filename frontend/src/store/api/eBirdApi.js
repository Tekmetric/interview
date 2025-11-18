import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

// eBird API 2.0 base configuration
export const eBirdApi = createApi({
  reducerPath: "eBirdApi",
  baseQuery: fetchBaseQuery({
    baseUrl: "https://api.ebird.org/v2/",
    prepareHeaders: (headers) => {
      // Add your eBird API key here
      // You can get a free API key from: https://ebird.org/api/keygen
      const apiKey = "qn7t8jmf0qcc";
      headers.set("X-eBirdApiToken", apiKey);
      return headers;
    },
  }),
  tagTypes: ["Observations", "Species", "Regions"],
  endpoints: (builder) => ({
    // Get recent observations in a region
    getRecentObservations: builder.query({
      query: ({ regionCode = "US", back = 7, maxResults = 50 } = {}) =>
        `data/obs/${regionCode}/recent?back=${back}&maxResults=${maxResults}`,
      providesTags: ["Observations"],
    }),

    // Get recent notable observations in a region
    getRecentNotableObservations: builder.query({
      query: ({ regionCode = "US", back = 7, maxResults = 50 } = {}) =>
        `data/obs/${regionCode}/recent/notable?back=${back}&maxResults=${maxResults}`,
      providesTags: ["Observations"],
    }),

    // Get recent observations of a species in a region
    getRecentSpeciesObservations: builder.query({
      query: ({
        regionCode = "US",
        speciesCode,
        back = 30,
        maxResults = 50,
      } = {}) =>
        `data/obs/${regionCode}/recent/${speciesCode}?back=${back}&maxResults=${maxResults}`,
      providesTags: ["Observations"],
    }),

    // Get eBird taxonomy
    getTaxonomy: builder.query({
      query: ({ fmt = "json", locale = "en" } = {}) =>
        `ref/taxonomy/ebird?fmt=${fmt}&locale=${locale}`,
      providesTags: ["Species"],
    }),

    // Get sub-regions
    getSubRegions: builder.query({
      query: ({ regionType = "subnational1", parentRegionCode = "US" } = {}) =>
        `ref/region/list/${regionType}/${parentRegionCode}`,
      providesTags: ["Regions"],
    }),
  }),
});

// Export hooks for usage in functional components
export const {
  useGetRecentObservationsQuery,
  useGetRecentNotableObservationsQuery,
  useGetRecentSpeciesObservationsQuery,
  useGetTaxonomyQuery,
  useGetSubRegionsQuery,
} = eBirdApi;
