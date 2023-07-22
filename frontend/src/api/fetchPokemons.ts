const POKEMON_API_SERVER = 'https://pokeapi.co/api/v2'

export interface Pokemon {
  name: string
  url?: string
}

export interface PaginatedPokemonsResponse {
  results: Pokemon[]
}

/**
 * Method for fetching data from Pokemon API
 * Returns a promise because it will be used from react-query
 */
export async function fetchPokemons(): Promise<PaginatedPokemonsResponse> {
  const apiUrl = `${POKEMON_API_SERVER}/pokemon`
  const response = await fetch(apiUrl)

  if (!response.ok) {
    return Promise.reject(`Error fetching from ${apiUrl}`)
  }

  return response.json()
}
