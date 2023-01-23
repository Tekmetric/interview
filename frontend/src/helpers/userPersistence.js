const USER_KEY = 'user';
const LANG_KEY = 'lang';
const SEARCH_FILTERS_KEY = 'search';

export const userPersistence = {
  setUser: (user) => {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  },

  user: () => {
    return localStorage.key(USER_KEY)
      ? JSON.parse(localStorage.getItem(USER_KEY))
      : null;
  },

  accessToken: () => {
    return JSON.parse(localStorage.getItem(USER_KEY))?.accessToken;
  },

  refreshToken: () => {
    return this.user()?.refreshToken;
  },

  removeUser: () => {
    localStorage.removeItem(USER_KEY);
  },

  setLanguage: (lang) => {
    localStorage.setItem(LANG_KEY, JSON.stringify(lang));
  },

  getLanguage: () => {
    return localStorage[LANG_KEY]
      ? JSON.parse(localStorage.getItem(LANG_KEY))
      : 'en';
  },

  setSearchFilters: (filters) => {
    localStorage.setItem(SEARCH_FILTERS_KEY, JSON.stringify(filters));
  },

  getSearchFilters: () => {
    return localStorage[SEARCH_FILTERS_KEY]
      ? JSON.parse(localStorage.getItem(SEARCH_FILTERS_KEY))
      : {
          location: null,
          startDay: new Date(),
          endDay: new Date(),
          guests: { adults: 1, children: 0 },
        };
  },
};
