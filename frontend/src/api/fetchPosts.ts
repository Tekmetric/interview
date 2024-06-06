import axios from "axios";

export async function fetchPosts(page: number) {
  try {
    // perpage is static for demo purposes
    const response = await axios.get(
      `http://localhost:3000/posts?_page=${page}&_per_page=6`
    );

    // Simulate slow network
    await new Promise((resolve) => setTimeout(resolve, 2000));

    return response.data;
  } catch (error) {
    console.error(error);
  }
}
