import axios from "axios";

export async function fetchUsers() {
  try {
    const response = await axios.get("http://localhost:3000/users");

    // Simulate slow network
    await new Promise((resolve) => setTimeout(resolve, 2000));

    return response.data;
  } catch (error) {
    console.error(error);
  }
}
