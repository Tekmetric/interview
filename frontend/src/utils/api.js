const queryString = require('query-string');

const API_PREFIX = 'https://rickandmortyapi.com/api';

export default {
  fetchCharacters: async (name = '', page = 1) => {
    const qs = queryString.stringify({ name, page }, { skipEmptyString: true });
    const response = await fetch(API_PREFIX + `/character?${qs}`);
    return response.json();
  },
};
