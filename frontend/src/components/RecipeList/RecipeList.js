import React from "react";
import RecipeCard from "../RecipeCard/RecipeCard";
import { RecipeListStyles } from "./styles";

const RecipeList = ({ recipes }) => {
  const styles = RecipeListStyles;
  return (
    <div style={styles.listContainer}>
      {recipes.map((recipe) => (
        <RecipeCard key={recipe.id} recipe={recipe} />
      ))}
    </div>
  );
};

export default RecipeList;
