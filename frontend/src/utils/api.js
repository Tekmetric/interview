const queryString = require('query-string');

const API_PREFIX = process.env.REACT_APP_API_PREFIX;

export default {
  fetchCharacters: async (name = '', page = 1) => {
    const qs = queryString.stringify({ name, page }, { skipEmptyString: true });
    const response = await fetch(API_PREFIX + `/character?${qs}`);
    return response.json();
  },
};
