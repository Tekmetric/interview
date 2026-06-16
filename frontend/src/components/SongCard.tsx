import { Heart } from "lucide-react";
import { useStore } from "@tanstack/react-store";
import trackStore, { toggleLike } from "@/stores/trackStore";
import { motion } from "motion/react";

export interface SongCardProps {
  id: string;
  title: string;
  artist: string;
  album: string;
  image: string;
  isLiked?: boolean;
  primaryGenreName: string;
}

export function SongCard({
  id,
  title,
  artist,
  album,
  image,
  primaryGenreName,
}: SongCardProps) {
  const likedTracks = useStore(trackStore, (state) => state.likedTracks);
  const liked = likedTracks.has(id);

  const handleLike = () => {
    toggleLike(id);
  };

  return (
    <div className="flex flex-row w-full items-center">
      <img
        src={image}
        alt={title}
        className="w-10 h-10 border border-border rounded-md"
      />
      <div className="ml-4 w-full flex-1">
        <span className="font-bold">{title}</span>
        <div className="flex flex-row gap-2">
          <span>{artist}</span>
          <span>•</span>
          <span>{album}</span>
        </div>
      </div>
      <motion.button
        className="w-10 h-10 flex items-center justify-center cursor-pointer rounded-full transition-colors"
        onClick={handleLike}
        whileHover={{ scale: 1.5 }}
        whileTap={{ scale: 0.8 }}
      >
        <Heart
          className={`w-5 h-5 ${
            liked ? "fill-tomato stroke-tomato" : "fill-none stroke-onyx"
          }`}
        />
      </motion.button>
    </div>
  );
}
