import { useQuery, useSuspenseQuery } from "@tanstack/react-query";
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
import { User } from "../../types/User";
import { fetchUsers } from "../../api/fetchUsers";
import { useGetAvatarSrc } from "../../hooks/useGetAvatarSrc";

export const PostsList = () => {
  const { selectedPost, onClearSelection, onPostClick } = usePostListState();
  const getAvatarSrc = useGetAvatarSrc();

  const { data: posts } = useSuspenseQuery<Array<Post>>({
    queryKey: ["posts"],
    queryFn: fetchPosts,
  });

  const { data: users } = useQuery<Array<User>>({
    queryKey: ["users"],
    queryFn: fetchUsers,
    staleTime: 1000 * 60 * 5,
  });

  return (
    <div>
      <List>
        {posts.map((post) => {
          const isSelected = selectedPost?.id === post.id;
          const author = users?.find((user) => user.id === post.authorId);
          const avatarUrl = author
            ? getAvatarSrc(author.avatarFile)
            : undefined;

          return (
            <ListItemButton
              key={post.id}
              onClick={() => onPostClick(post)}
              selected={isSelected}
              divider
            >
              <ListItemAvatar>
                <Avatar src={avatarUrl}  />
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
