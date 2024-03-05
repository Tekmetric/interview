import { ContentData } from './ContentData';

export type ContentContext = {
  loading: boolean;
  data?: ContentData;
  error?: string;
};

export type MovieAPIResponse = {
  totalResults: number;
  Search: Array<{
    Title: string;
    Year: string;
    imdbID: string;
    Type: string;
    Poster: string;
  }>;
};

export type DescriptionAPIResponse = {
  articles: Array<{
    description: string;
  }>;
};
