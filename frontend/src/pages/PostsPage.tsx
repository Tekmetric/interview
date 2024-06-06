import { Avatar, Container, Paper, Typography } from "@mui/material";
import { makeStyles } from "tss-react/mui";
import { Suspense } from "react";
import { PostsList } from "../components/posts-list/PostsList";
import { SkeletonList } from "../components/skeleton-list/SkeletonList";
import { useAuthenticationContext } from "../hooks/useAuthenticationContext";
import { useGetAvatarSrc } from "../hooks/useGetAvatarSrc";

const useStyles = makeStyles()((theme) => ({
  root: {
    padding: theme.spacing(2),
    marginTop: theme.spacing(10),
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  user: {
    display: "flex",
    alignItems: "center",
    gap: theme.spacing(1),
  },
}));

export const PostsPage = () => {
  const { classes } = useStyles();
  const { user } = useAuthenticationContext();
  const getAvatarSrc = useGetAvatarSrc();

  return (
    <Container maxWidth="md" component={Paper} className={classes.root}>
      <div className={classes.header}>
        <Typography variant="h4" mb={2}>
          Posts
        </Typography>

        {user && (
          <div className={classes.user}>
            <Typography>Welcome back, {user.username}!</Typography>
            <Avatar src={getAvatarSrc(user.avatarFile)} />
          </div>
        )}
      </div>
      <Suspense fallback={<SkeletonList rows={7} />}>
        <PostsList />
      </Suspense>
    </Container>
  );
};
