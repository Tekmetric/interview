import axios from 'axios';

/**
 * Fetches a list of temporary users and returns a random user.
 * @returns A random user object from JSONPlaceholder API.
 */
export async function getRandomUser() {
    try {
        const response = await axios.get('https://jsonplaceholder.typicode.com/users');
        const users = response.data;

        if (!Array.isArray(users) || users.length === 0) {
            throw new Error("No users found.");
        }

        // Generate a random index
        const randomIndex = Math.floor(Math.random() * users.length);
        return users[5]; // Return a random user
    } catch (error) {
        console.error("Error fetching users:", error);
        throw error;
    }
}
