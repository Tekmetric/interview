export interface WatchmodeResult {
  name: string;
  relevance: number;
  type: string;
  id: number;
  year?: number | null;
  result_type: string;
  imdb_id?: string | null;
  tmdb_id?: number | null;
  tmdb_type?: string | null;
  image_url?: string | null;
}