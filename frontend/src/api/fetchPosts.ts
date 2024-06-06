import axios from "axios";
import { baseUrl, delayNetwork } from "./common";

export async function fetchPosts(page: number) {
  try {
    // perpage is static for demo purposes
    const response = await axios.get(
      `${baseUrl}/posts?_page=${page}&_per_page=6`
    );

    // Simulate slow network
    await delayNetwork();

    return response.data;
  } catch (error) {
    console.error(error);
  }
}
