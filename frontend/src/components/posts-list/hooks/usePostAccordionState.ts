import { useState } from "react";
import { Post } from "../../../types/Post";

export function usePostListState() {
  const [selectedPost, setSelectedPost] = useState<Post | null>(null);

  const onPostClick = async (post: Post) => {
    setSelectedPost(post);
  };

  const onClearSelection = () => {
    setSelectedPost(null);
  };

  return { selectedPost, onPostClick, onClearSelection };
}
