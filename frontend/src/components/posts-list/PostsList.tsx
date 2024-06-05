import { useSuspenseQuery } from "@tanstack/react-query";
import { Post } from "../../types/Post";
import {
  Avatar,
  List,
  ListItemAvatar,
  ListItemButton,
  ListItemText,
} from "@mui/material";
import { fetchPosts } from "../../api/fetchPosts";
import { usePostListState } from "./hooks/usePostAccordionState";
import { PostDetailDrawer } from "../post-detail-drawer/PostDetailDrawer";
import { ChevronRight } from "@mui/icons-material";

export const PostsList = () => {
  const { selectedPost, onClearSelection, onPostClick } = usePostListState();

  const { data } = useSuspenseQuery<Array<Post>>({
    queryKey: ["posts"],
    queryFn: () => fetchPosts(),
  });

  return (
    <div>
      <List>
        {data.map((post) => {
          const isSelected = selectedPost?.id === post.id;

          return (
            <ListItemButton
              key={post.id}
              onClick={() => onPostClick(post)}
              selected={isSelected}
              divider
            >
              <ListItemAvatar>
                <Avatar alt="Remy Sharp" src="/static/images/avatar/1.jpg" />
              </ListItemAvatar>
              <ListItemText primary={post.title} />
              <ChevronRight />
            </ListItemButton>
          );
        })}
      </List>

      <PostDetailDrawer
        open={selectedPost !== null}
        onClose={onClearSelection}
        selectedPost={selectedPost}
      />
    </div>
  );
};
