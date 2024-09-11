import React, { useEffect, useState } from "react";
import { getInitialRecipes, searchRecipes } from "./api";
import RecipeList from "./components/RecipeList/RecipeList";
import RecipeSearch from "./components/RecipeSearch/RecipeSearch";
import { appStyles } from "./styles";

const App = () => {
  const [recipes, setRecipes] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [noResults, setNoResults] = useState(false);
  const [loading, setLoading] = useState(false);

  const styles = appStyles;

  // Fetch initial recipes on component mount
  useEffect(() => {
    const fetchInitialRecipes = async () => {
      setLoading(true);
      const initialRecipes = await getInitialRecipes();
      console.log(initialRecipes);
      setRecipes(initialRecipes);
      setLoading(false);
    };
    fetchInitialRecipes();
  }, []);

  const handleSearch = async (e) => {
    e.preventDefault();
    setRecipes([]);
    setLoading(true);

    const searchResults = await searchRecipes(searchTerm);
    if (searchResults.length > 0) {
      setRecipes(searchResults);
      setNoResults(false);
    } else {
      setRecipes([]);
      setNoResults(true);
    }
    setLoading(false);
  };

  return (
    <div style={styles.appContainer}>
      <header>
        <h2>Recipe App</h2>
        <RecipeSearch
          searchTerm={searchTerm}
          setSearchTerm={setSearchTerm}
          handleSearch={handleSearch}
          loading={loading}
        />

        {loading && <div style={styles.loadingMessage}>Loading...</div>}
        {noResults && !loading && (
          <div style={styles.noResults}>No results found for your search</div>
        )}
      </header>
      {!loading && <RecipeList recipes={recipes} />}
    </div>
  );
};

export default App;
