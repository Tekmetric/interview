import axios from "axios";
import { Comment } from "../types/Comment";

export function postComment(newComment: Partial<Comment>) {
  return axios.post("http://localhost:3000/comments", newComment);
}
