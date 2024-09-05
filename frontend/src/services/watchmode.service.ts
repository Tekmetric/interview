import { WATCHMODE_API_KEY } from "../config";

export const fetchAutocompletSearch = (search: string) => {
  return fetch(`https://api.watchmode.com/v1/autocomplete-search/?apiKey=${WATCHMODE_API_KEY}&search_value=${search}&search_type=1`)
    .then(res => res.json());
};
