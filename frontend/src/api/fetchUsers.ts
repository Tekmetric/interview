import axios from "axios";
import { baseUrl, delayNetwork } from "./common";

export async function fetchUsers() {
  try {
    const response = await axios.get(`${baseUrl}/users`);

    // Simulate slow network
    await delayNetwork();

    return response.data;
  } catch (error) {
    console.error(error);
  }
}
