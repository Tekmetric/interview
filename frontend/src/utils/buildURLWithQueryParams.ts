const buildURLWithQueryParams = (
  url: string | undefined,
  queryParmams: Record<string, unknown>,
): string | null => {
  if (!url) {
    return null;
  }
  const params = Object.entries(queryParmams)
    .filter(([key, value]) => !!key && !!value)
    .map(
      ([key, value]) =>
        `${key}=${encodeURIComponent(value as string | number | boolean)}`,
    )
    .join('&');
  return `${url}/?${params}`;
};

export const buildMovieAPIURL = (queryText: string, page: number) =>
  buildURLWithQueryParams(process.env.REACT_APP_MOVIE_API_URL, {
    apiKey: process.env.REACT_APP_MOVIE_API_KEY,
    s: `*${queryText}*`,
    page,
  }) ?? '';

export const buildDescriptionAPIURL = (queryText: string) =>
  buildURLWithQueryParams(process.env.REACT_APP_TEXT_API_URL, {
    apiKey: process.env.REACT_APP_TEXT_API_KEY,
    q: queryText,
  }) ?? '';

export default buildURLWithQueryParams;
