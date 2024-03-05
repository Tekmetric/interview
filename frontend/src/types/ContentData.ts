export type ContentData = {
  totalResults: number;
  hits: Array<MovieSearchHit>;
};

export type MovieSearchHit = {
  imdbID: string;
  posterURL?: string;
  name: string;
  description?: string;
  yearStart: number;
  yearEnd?: number;
  type: string;
};
