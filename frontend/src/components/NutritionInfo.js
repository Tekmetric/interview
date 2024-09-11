import React from "react";

const NutritionInfo = ({ nutrition }) => {
  return (
    <div>
      <h3>Nutrition Information:</h3>
      <p>
        Calories: {nutrition && nutrition.calories ? nutrition.calories : "N/A"}
      </p>
      <p>
        Carbohydrates:{" "}
        {nutrition && nutrition.carbohydrates ? nutrition.carbohydrates : "N/A"}
      </p>
      <p>Fat: {nutrition && nutrition.fat ? nutrition.fat : "N/A"}</p>
      <p>
        Protein: {nutrition && nutrition.protein ? nutrition.protein : "N/A"}
      </p>
    </div>
  );
};

export default NutritionInfo;
