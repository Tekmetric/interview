import { useState, useEffect } from 'react';
import {
  DescriptionAPIResponse,
  MovieAPIResponse,
  ContentContext,
} from '../types/Response';
import { buildDescriptionAPIURL, buildMovieAPIURL } from '../utils/url';
import { sanitizeMovieData } from '../utils/sanitizer';

const useMovies = (queryText: string, page: number): ContentContext => {
  const [response, setResponse] = useState<ContentContext>({ loading: false });

  useEffect(() => {
    if (!queryText.length) {
      setResponse({
        data: response.data,
        loading: false,
      });
      return;
    }
    if (queryText.length < 3) {
      setResponse({
        data: response.data,
        loading: false,
        error: 'Search text must be at least 3 characters',
      });
      return;
    }
    if (page < 1) {
      setResponse({
        loading: false,
        error: 'Page must be at least 1',
        data: response.data,
      });
      return;
    }
    const movieURL = buildMovieAPIURL(queryText, page);
    const descriptionURL = buildDescriptionAPIURL(queryText);

    const moviePromise = fetch(movieURL).then((resp) => resp.json());
    const descriptionPromise = fetch(descriptionURL).then((resp) =>
      resp.json(),
    );

    setResponse({ loading: true });
    Promise.all([moviePromise, descriptionPromise])
      .then(([movieData, descriptionData]) => {
        const sanitizedContentData = sanitizeMovieData(
          movieData as MovieAPIResponse,
          descriptionData as DescriptionAPIResponse,
          page,
        );
        if (!sanitizedContentData.totalResults) {
          setResponse({
            loading: false,
            data: response.data,
            error: 'Query returned no results. Try another one',
          });
          return;
        }

        setResponse({ loading: false, data: sanitizedContentData });
      })
      .catch((error) => {
        console.warn(error);
        setResponse({
          loading: false,
          error: 'Something went wrong. Please try again later',
          data: response.data,
        });
      });
  }, [queryText, page]);

  return response;
};

export default useMovies;
