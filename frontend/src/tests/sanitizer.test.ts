import { ContentData } from '../types/ContentData';
import { DescriptionAPIResponse, MovieAPIResponse } from '../types/Response';
import { sanitizeMovieData } from '../utils/sanitizer';

const mockMovieData: MovieAPIResponse = {
  totalResults: 3,
  Search: [
    {
      imdbID: 'test1',
      Title: 'Good Will Testing',
      Year: '2004–2009',
      Type: 'movie',
      Poster: 'https://aws.com/assets/poster.jpg',
    },
    {
      imdbID: 'test2',
      Title: 'The Dark Test',
      Year: '2009',
      Type: 'game',
      Poster: 'https://aws.com/assets/poster.png',
    },
    {
      imdbID: 'test3',
      Title: 'Shawshank Retesting',
      Year: '2004',
      Type: 'movie',
      Poster: 'https://aws.com/assets/poster.html',
    },
  ],
};

const mockDescriptionData: DescriptionAPIResponse = {
  articles: [
    {
      description: 'Lorem ipsum',
    },
    {
      description: 'Lorem ipsum 2',
    },
  ],
};

describe('Sanitizer test', () => {
  const expected: ContentData = {
    totalResults: 3,
    hits: [
      {
        imdbID: 'test1',
        name: 'Good Will Testing',
        description: 'Lorem ipsum',
        yearStart: 2004,
        yearEnd: 2009,
        type: 'movie',
        posterURL: 'https://aws.com/assets/poster.jpg',
      },
      {
        imdbID: 'test2',
        name: 'The Dark Test',
        description: 'Lorem ipsum 2',
        yearStart: 2009,
        type: 'game',
        posterURL: 'https://aws.com/assets/poster.png',
      },
      {
        imdbID: 'test3',
        description: 'This is a placeholder description',
        name: 'Shawshank Retesting',
        yearStart: 2004,
        type: 'movie',
      },
    ],
  };

  it('should return the sanitized movie data', () => {
    const actual = sanitizeMovieData(mockMovieData, mockDescriptionData, 1);
    expect(actual.totalResults).toEqual(3);
    expect(actual).toEqual(expected);
  });
});
