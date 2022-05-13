const API_PREFIX = 'https://rickandmortyapi.com/api';

export default {
    fetchCharacters: async (page = 1) => {
        const response = await fetch(API_PREFIX + `/character?page=${page}`);
        return response.json();
    },
};
