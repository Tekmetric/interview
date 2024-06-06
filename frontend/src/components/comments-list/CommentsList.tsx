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
import { useGetAvatarSrc } from "../../hooks/useGetAvatarSrc";
import { fetchUsers } from "../../api/fetchUsers";
import { User } from "../../types/User";

interface Props {
  postId: string;
}

export const CommentsList = ({ postId }: Props) => {
  const getAvatarSrc = useGetAvatarSrc();

  const { data } = useSuspenseQuery<Array<Comment>>({
    queryKey: ["comments", postId],
    queryFn: () => fetchComments(postId),
  });

  const { data: users } = useSuspenseQuery<Array<User>>({
    queryKey: ["users"],
    queryFn: fetchUsers,
    staleTime: 1000 * 60 * 5,
  });

  return (
    <List dense>
      {data.map((comment) => {
        const author = users?.find((user) => user.id === comment.authorId);
        const avatarSrc = author ? getAvatarSrc(author.avatarFile) : undefined;

        return (
          <ListItem key={comment.id}>
            <ListItemAvatar>
              <Avatar src={avatarSrc} />
            </ListItemAvatar>
            <ListItemText primary={comment.text} />
          </ListItem>
        );
      })}
    </List>
  );
};
