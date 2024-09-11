import React, { useState } from "react";
import { getRecipeDetails } from "../../api";

import Instructions from "../Instructions";
import NutritionInfo from "../NutritionInfo";
import { RecipeCardStyles } from "./styles";

const RecipeCard = ({ recipe }) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [recipeDetails, setRecipeDetails] = useState(null);
  const [loading, setLoading] = useState(false);
  const styles = RecipeCardStyles;

  const handleCardClick = async () => {
    if (!isExpanded) {
      setLoading(true);
      setIsExpanded(true);
      const details = await getRecipeDetails(recipe.id);
      setRecipeDetails(details);
      setLoading(false);
    } else {
      setIsExpanded(false);
    }
  };

  return (
    <div
      style={
        isExpanded ? { ...styles.card, ...styles.cardExpanded } : styles.card
      }
    >
      <img src={recipe.thumbnail_url} alt={recipe.name} style={styles.image} />
      <div style={styles.content}>
        <p style={styles.category}>COOKING</p>
        <h2 style={styles.title}>{recipe.name}</h2>
        <p
          style={
            isExpanded ? styles.fullDescription : styles.truncatedDescription
          }
        >
          {recipe.description || "This is a delicious recipe to try at home!"}
        </p>

        {isExpanded && (
          <div style={styles.expandedContent}>
            {loading ? (
              <p>Loading recipe details...</p>
            ) : recipeDetails ? (
              <div style={styles.scrollableContent}>
                <Instructions instructions={recipeDetails.instructions} />
                <NutritionInfo nutrition={recipeDetails.nutrition} />
              </div>
            ) : (
              <p>Recipe details could not be loaded.</p>
            )}
          </div>
        )}
        <button onClick={handleCardClick} style={styles.viewMoreButton}>
          {isExpanded ? "View Less" : "View More"}
        </button>
      </div>
    </div>
  );
};

export default RecipeCard;
