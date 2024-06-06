import axios from "axios";
import { Comment } from "../types/Comment";
import { baseUrl } from "./common";

export function postComment(newComment: Partial<Comment>) {
  return axios.post(`${baseUrl}/comments`, newComment);
}
