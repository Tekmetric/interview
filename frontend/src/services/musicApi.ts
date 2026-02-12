import { SongCardProps } from "@/components/SongCard";

const SEARCH_URL = "https://itunes.apple.com/search";
const LOOKUP_URL = "https://itunes.apple.com/lookup";

export const searchTracks = async (
  searchTerm: string
): Promise<SongCardProps[]> => {
  if (!searchTerm.trim()) return [];

  const response = await fetch(
    `${SEARCH_URL}?term=${encodeURIComponent(
      searchTerm
    )}&media=music&entity=song&limit=10`
  );

  if (!response.ok) {
    throw new Error("Search failed");
  }

  const data = await response.json();
  return data.results.map((song: any) => ({
    id: song.trackId,
    isLiked: false,
    title: song.trackName,
    artist: song.artistName,
    album: song.collectionName,
    image: song.artworkUrl100,
    primaryGenreName: song.primaryGenreName,
  }));
};

export const lookupTracks = async (
  trackIds: string[]
): Promise<SongCardProps[]> => {
  if (!trackIds.length) return [];

  // iTunes lookup supports up to 200 IDs per request
  const idsToLookup = trackIds.slice(0, 200);
  const idsParam = idsToLookup.join(",");

  const response = await fetch(`${LOOKUP_URL}?id=${idsParam}&entity=song`);

  if (!response.ok) {
    throw new Error("Lookup failed");
  }

  const data = await response.json();
  return data.results.map((song: any) => ({
    id: song.trackId,
    isLiked: false,
    title: song.trackName,
    artist: song.artistName,
    album: song.collectionName,
    image: song.artworkUrl100,
    primaryGenreName: song.primaryGenreName,
  }));
};
