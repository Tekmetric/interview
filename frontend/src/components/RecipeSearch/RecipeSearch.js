import React from "react";
import { recipeSearchStyles } from "./styles";

const RecipeSearch = ({ searchTerm, setSearchTerm, handleSearch, loading }) => {
  const styles = recipeSearchStyles;
  return (
    <div style={styles.searchContainer}>
      <input
        type="text"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        placeholder="Search for a recipe"
        style={styles.searchInput}
      />
      <button
        type="submit"
        style={styles.searchButton}
        onClick={handleSearch}
        disabled={loading}
      >
        {loading ? "Searching..." : "Search"}
      </button>
    </div>
  );
};

export default RecipeSearch;
