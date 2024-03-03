import { ContentData, ContentType } from '../types/ContentData';
import { DescriptionAPIResponse, MovieAPIResponse } from '../types/Response';

const sanitizeYear = (years: string): [number, number | undefined] => {
  const yearRange = years.split('-');
  if (yearRange.length === 2) {
    return [Number.parseInt(yearRange[0]), Number.parseInt(yearRange[1])];
  }
  return [Number.parseInt(years), undefined];
};

const sanitizePoster = (posterURL: string): string | undefined => {
  const isPosterImage = posterURL.endsWith('jpg') || posterURL.endsWith('.png');
  return isPosterImage ? posterURL : undefined;
};

export const sanitizeMovieData = (
  movieData: MovieAPIResponse,
  descriptionData: DescriptionAPIResponse,
  page: number,
): ContentData[] => {
  return movieData.Search.map((item, index) => {
    const realIndex =
      ((process.env.REACT_APP_MOVIE_API_PAGE_SIZE as unknown as number) || 0) *
        page +
      index;
    const years = sanitizeYear(item.Year);
    return {
      imdbID: item.imdbID,
      posterURL: sanitizePoster(item.Poster),
      name: item.Title,
      description: descriptionData.articles[realIndex].description,
      type: item.Type === 'movie' ? ContentType.MOVIE : ContentType.SERIES,
      yearStart: years[0],
      yearEnd: years[1],
    };
  });
};
