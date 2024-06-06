import { ChevronRight } from "@mui/icons-material";
import {
  Avatar,
  List,
  ListItemAvatar,
  ListItemButton,
  ListItemText,
  Pagination,
  Typography,
} from "@mui/material";
import { useQuery, useSuspenseQuery } from "@tanstack/react-query";
import { makeStyles } from "tss-react/mui";
import { fetchPosts } from "../../api/fetchPosts";
import { fetchUsers } from "../../api/fetchUsers";
import { useGetAvatarSrc } from "../../hooks/useGetAvatarSrc";
import { PaginatedResponsePayload } from "../../types/PaginatedResponsePayload";
import { Post } from "../../types/Post";
import { User } from "../../types/User";
import { PostDetailDrawer } from "../post-detail-drawer/PostDetailDrawer";
import { usePostListState } from "./hooks/usePostAccordionState";
import { usePostsListPagination } from "./hooks/usePostsListPagination";

const useStyles = makeStyles()((theme) => ({
  footer: {
    display: "flex",
    justifyContent: "space-between",
    marginTop: theme.spacing(2),
  },
}));

export const PostsList = () => {
  const { classes } = useStyles();
  const { selectedPost, onClearSelection, onPostClick } = usePostListState();
  const { currentPage, handleChangePage } = usePostsListPagination();
  const getAvatarSrc = useGetAvatarSrc();

  const { data: postsResponse } = useSuspenseQuery<
    PaginatedResponsePayload<Post>
  >({
    queryKey: ["posts", currentPage],
    queryFn: () => fetchPosts(currentPage),
  });

  const { data: users } = useQuery<Array<User>>({
    queryKey: ["users"],
    queryFn: fetchUsers,
    staleTime: 1000 * 60 * 5,
  });

  const { data: posts, items: itemCount, pages: pageCount } = postsResponse;

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
                <Avatar src={avatarUrl} />
              </ListItemAvatar>
              <ListItemText primary={post.title} />

              <ChevronRight />
            </ListItemButton>
          );
        })}
      </List>

      <div className={classes.footer}>
        <Typography>Total: {itemCount}</Typography>
        <Pagination
          count={pageCount}
          variant="outlined"
          page={currentPage}
          onChange={handleChangePage}
        />
      </div>

      <PostDetailDrawer
        open={selectedPost !== null}
        onClose={onClearSelection}
        selectedPost={selectedPost}
      />
    </div>
  );
};
