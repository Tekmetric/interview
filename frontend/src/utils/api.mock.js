jest.mock('./api', () => ({
  fetchCharacters: jest.fn().mockImplementation((page) => {
    const PAYLOAD1 = {
      info: {
        count: 3,
        pages: 2,
        next: 'https://rickandmortyapi.com/api/character?page=2',
        prev: null,
      },
      results: [
        {
          id: 1,
          name: 'Rick Sanchez',
          status: 'Alive',
          species: 'Human',
          location: { name: 'Citadel of Ricks', url: 'https://rickandmortyapi.com/api/location/3' },
          image: 'https://rickandmortyapi.com/api/character/avatar/1.jpeg',
          url: 'https://rickandmortyapi.com/api/character/1',
        },
        {
          id: 2,
          name: 'Morty Smith',
          status: 'Alive',
          species: 'Human',
          location: { name: 'Citadel of Ricks', url: 'https://rickandmortyapi.com/api/location/3' },
          image: 'https://rickandmortyapi.com/api/character/avatar/2.jpeg',
          url: 'https://rickandmortyapi.com/api/character/2',
        },
      ],
    };
    const PAYLOAD2 = {
      info: {
        count: 3,
        pages: 2,
        next: null,
        prev: 'https://rickandmortyapi.com/api/character?page=1',
      },
      results: [
        {
          id: 3,
          name: 'Summer Smith',
          status: 'Alive',
          species: 'Human',
          location: {
            name: 'Earth (Replacement Dimension)',
            url: 'https://rickandmortyapi.com/api/location/20',
          },
          image: 'https://rickandmortyapi.com/api/character/avatar/3.jpeg',
          url: 'https://rickandmortyapi.com/api/character/3',
        },
      ],
    };

    return new Promise((resolve, reject) => {
      if (page === 1) {
        resolve(PAYLOAD1);
      } else if (page === 2) {
        resolve(PAYLOAD2);
      } else {
        reject();
      }
    });
  }),
}));
