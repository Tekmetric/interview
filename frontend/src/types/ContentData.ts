export type ContentData = {
  imdbID: string;
  posterURL?: string;
  name: string;
  description?: string;
  yearStart: number;
  yearEnd?: number;
  type: ContentType;
};

export enum ContentType {
  MOVIE,
  SERIES,
}
