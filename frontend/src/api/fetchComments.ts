import axios from "axios";
import { baseUrl, delayNetwork } from "./common";

export async function fetchComments(postId: string) {
  try {
    const response = await axios.get(`${baseUrl}/comments?postId=${postId}`);

    // Simulate slow network
    await delayNetwork();

    return response.data;
  } catch (error) {
    console.error(error);
  }
}
