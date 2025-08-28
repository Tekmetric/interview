export type Brewery = {
  id: string
  name: string
  brewery_type: string
  street: string | null
  address_2: string | null
  address_3: string | null
  city: string | null
  state: string | null
  county_province: string | null
  postal_code: string | null
  country: string | null
  longitude: string | null
  latitude: string | null
  phone: string | null
  website_url: string | null
  updated_at?: string | null
  created_at?: string | null
}

const API_BASE = "https://api.openbrewerydb.org/v1";

export async function fetchRandomBreweries(count: number = 4): Promise<Brewery[]> {
  const url = `${API_BASE}/breweries/random?size=${count}`;
  const res = await fetch(url, { headers: { "accept": "application/json" } });

  if (!res.ok) {
    throw new Error(`Failed to fetch breweries: ${res.status}`);
  }

  return (await res.json()) as Brewery[];
}

export async function searchBreweriesByQuery(query: string, limit: number = 10): Promise<Brewery[]> {
  const url = `${API_BASE}/breweries/search?query=${encodeURIComponent(query)}&per_page=${limit}`;
  const res = await fetch(url, { headers: { "accept": "application/json" } });

  if (!res.ok) {
    throw new Error(`Failed to search breweries: ${res.status}`);
  }

  return (await res.json()) as Brewery[];
}

export type BreweryAutocomplete = {
  id: string
  name: string
  brewery_type?: string
  city?: string
  state?: string
};

export async function fetchBreweriesAutocomplete(query: string, limit: number = 8): Promise<BreweryAutocomplete[]> {
  if (!query || query.trim().length < 3) return [];

  const url = `${API_BASE}/breweries/autocomplete?query=${encodeURIComponent(query)}&per_page=${limit}`;
  const res = await fetch(url, { headers: { "accept": "application/json" } });

  if (!res.ok) {
    throw new Error(`Failed to autocomplete breweries: ${res.status}`);
  }

  return (await res.json()) as BreweryAutocomplete[];
}

export async function fetchBreweryById(id: string): Promise<Brewery | null> {
  const url = `${API_BASE}/breweries/${encodeURIComponent(id)}`;
  const res = await fetch(url, { headers: { "accept": "application/json" } });

  if (!res.ok) return null;

  return (await res.json()) as Brewery | null;
}


