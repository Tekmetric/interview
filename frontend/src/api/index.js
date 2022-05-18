const queryString = require('query-string');

const API_PREFIX = process.env.REACT_APP_API_PREFIX;

const api = {
  fetchCharacters: async (name = '', page = 1) => {
    const qs = queryString.stringify({ name, page }, { skipEmptyString: true });
    const response = await fetch(`${API_PREFIX}/character?${qs}`);
    if (response.status === 404) {
      return {
        info: {
          pages: 0,
          count: 0,
        },
        results: [],
      };
    }
    return response.json();
  },
};

export default api;
