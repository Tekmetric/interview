import { useSuspenseQuery } from "@tanstack/react-query";
import { fetchComments } from "../../api/fetchComments";
import {
  Avatar,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
} from "@mui/material";
import { Comment } from "../../types/Comment";

interface Props {
  postId: string;
}

export const CommentsList = ({ postId }: Props) => {
  const { data } = useSuspenseQuery<Array<Comment>>({
    queryKey: ["comments", postId],
    queryFn: () => fetchComments(postId),
  });

  return (
    <List dense>
      {data.map((comment) => (
        <ListItem key={comment.id}>
          <ListItemAvatar>
            <Avatar alt="Remy Sharp" src="/static/images/avatar/1.jpg" />
          </ListItemAvatar>
          <ListItemText primary={comment.text} />
        </ListItem>
      ))}
    </List>
  );
};
