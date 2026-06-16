import { Store } from "@tanstack/react-store";

interface Track {
  id: string;
  title: string;
  artist: string;
  album: string;
  image: string;
  primaryGenreName: string;
}

interface TrackStoreState {
  tracks: Track[];
  likedTracks: Set<string>;
}

// Load liked tracks from localStorage
const loadLikedTracks = (): Set<string> => {
  const stored = localStorage.getItem("likedTracks");
  return stored ? new Set(JSON.parse(stored)) : new Set();
};

const trackStore = new Store<TrackStoreState>({
  tracks: [],
  likedTracks: loadLikedTracks(),
});

// Actions
export const toggleLike = (trackId: string) => {
  trackStore.setState((state) => {
    const newLikedTracks = new Set(state.likedTracks);

    if (newLikedTracks.has(trackId)) {
      newLikedTracks.delete(trackId);
    } else {
      newLikedTracks.add(trackId);
    }

    // Persist to localStorage
    localStorage.setItem(
      "likedTracks",
      JSON.stringify(Array.from(newLikedTracks))
    );

    return {
      ...state,
      likedTracks: newLikedTracks,
    };
  });
};

export const setTracks = (tracks: Track[]) => {
  trackStore.setState((state) => ({
    ...state,
    tracks,
  }));
};

export const isTrackLiked = (trackId: string): boolean => {
  return trackStore.state.likedTracks.has(trackId);
};

export default trackStore;
