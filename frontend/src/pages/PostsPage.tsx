import { Container, Paper, Typography } from "@mui/material";
import { makeStyles } from "tss-react/mui";
import { Suspense } from "react";
import { PostsList } from "../components/posts-list/PostsList";
import { SkeletonList } from "../components/skeleton-list/SkeletonList";

const useStyles = makeStyles()((theme) => ({
  root: {
    padding: theme.spacing(2),
    marginTop: theme.spacing(10),
  },
}));

export const PostsPage = () => {
  const { classes } = useStyles();

  return (
    <Container maxWidth="md" component={Paper} className={classes.root}>
      <Typography variant="h4" mb={2}>
        Posts
      </Typography>
      <Suspense fallback={<SkeletonList rows={6} />}>
        <PostsList />
      </Suspense>
    </Container>
  );
};
