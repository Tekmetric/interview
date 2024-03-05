import {
  buildDescriptionAPIURL,
  buildImdbURL,
  buildMovieAPIURL,
} from '../utils/url';

describe('Url build functions tests', () => {
  beforeAll(() => {
    process.env.REACT_APP_TEXT_API_KEY = 'text-api-key';
    process.env.REACT_APP_MOVIE_API_KEY = 'movies-api-key';
    process.env.REACT_APP_MOVIE_API_URL = 'http://www.movies.com';
    process.env.REACT_APP_TEXT_API_URL = 'http://www.text.com';
  });

  it('should return the imdb url', () => {
    expect(buildImdbURL('test')).toEqual('https://imdb.com/title/test');
  });
  it('should return movie api url', () => {
    expect(buildMovieAPIURL('test query', 1)).toEqual(
      'http://www.movies.com/?apiKey=movies-api-key&s=*test%20query*&page=1',
    );
  });
  it('should return text api url', () => {
    expect(buildDescriptionAPIURL('test query')).toEqual(
      'http://www.text.com/?apiKey=text-api-key&q=test%20query',
    );
  });
});
