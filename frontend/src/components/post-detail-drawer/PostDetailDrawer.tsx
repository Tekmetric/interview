import { Drawer, IconButton, TextField, Typography } from "@mui/material";
import { Suspense } from "react";
import { SkeletonList } from "../skeleton-list/SkeletonList";
import { CommentsList } from "../comments-list/CommentsList";
import { Post } from "../../types/Post";
import { makeStyles } from "tss-react/mui";
import { useCommentInputState } from "./hooks/useCommentInputState";
import { Send } from "@mui/icons-material";

const useStyles = makeStyles()((theme) => ({
  root: {
    width: 400,
    padding: theme.spacing(2),
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-between",
    height: "100%",
  },
  list: {
    display: "flex",
    flexDirection: "column",
    flexGrow: 1,
  },
  footer: {
    display: "flex",
    alignItems: "center",
    gap: theme.spacing(1),
    flexWrap: "nowrap",
  },
}));

interface Props {
  open: boolean;
  selectedPost: Post | null;
  onClose: () => void;
}

export const PostDetailDrawer = ({ open, onClose, selectedPost }: Props) => {
  const { classes } = useStyles();

  const { handleChange, value } = useCommentInputState();

  return (
    <Drawer anchor="right" open={open} onClose={onClose}>
      <div className={classes.root}>
        <Typography variant="h5" p={2}>
          Comments
        </Typography>
        <div className={classes.list}>
          <Suspense fallback={<SkeletonList />}>
            {selectedPost && <CommentsList postId={selectedPost.id} />}
          </Suspense>
        </div>

        <div className={classes.footer}>
          <TextField
            label="Comment"
            variant="outlined"
            value={value}
            onChange={handleChange}
            fullWidth
          />
          <IconButton>
            <Send color="primary"/>
          </IconButton>
        </div>
      </div>
    </Drawer>
  );
};
