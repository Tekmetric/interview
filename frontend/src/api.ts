const API_URL = 'https://openlibrary.org/search.json'

export interface OpenLibraryDoc {
  author_name: string[];
  first_publish_year: number;
  isbn?: string[];
  ia?: string[];
  id?: string[];
  cover_i?: number;
  title: string;
  key: string;
}

export interface OpenLibrarySearchResponse {
  numFound: number;
  start: number;
  numFoundExact: boolean;
  num_found?: number;
  offset?: number | null;
  docs: OpenLibraryDoc[];
}


export const getReactBooks = async (perPage: number, pageNumber: number): Promise<OpenLibrarySearchResponse> => {
  const response = await fetch(`${API_URL}?limit=${perPage}&page=${pageNumber}&q=react&fields=title,author_name,first_publish_year,isbn,cover_i,ia,id,key`)
  // Left it out for referencing other fields if needed later, the API docs are very sparse.
  // const response = await fetch(`${API_URL}?limit=${perPage}&q=react`)
  if (!response.ok) {
    throw new Error('Failed to fetch books')
  }
  return response.json()
}