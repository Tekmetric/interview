import axios from "axios";
// This should be encrypted in either Environment Variables or should be in the backend for the API Endpoints
const API_KEY = "a0cd9cbdebmsh4b2344c51f040abp1b1a33jsn82d65fff92a8";
const API_HOST = "tasty.p.rapidapi.com";

const apiClient = axios.create({
  baseURL: `https://${API_HOST}`,
  headers: {
    "x-rapidapi-key": API_KEY,
    "x-rapidapi-host": API_HOST,
  },
});

// Fetch 10 recipes to display on load
export const getInitialRecipes = async () => {
  try {
    const response = await apiClient.get("/recipes/list", {
      params: { from: 0, size: 10 },
    });
    return response.data.results;
  } catch (error) {
    console.error("Error fetching initial recipes:", error);
    return [];
  }
};

// Search recipes based on user input
export const searchRecipes = async (query) => {
  try {
    const response = await apiClient.get("/recipes/list", {
      params: { from: 0, size: 100, q: query },
    });
    return response.data.results;
  } catch (error) {
    console.error("Error searching recipes:", error);
    return [];
  }
};

// Get Recipe Details
export const getRecipeDetails = async (id) => {
  try {
    const response = await apiClient.get("/recipes/get-more-info", {
      params: { id },
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching recipe details:", error);
    return null;
  }
};
