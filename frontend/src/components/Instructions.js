import React from "react";

const Instructions = ({ instructions }) => {
  return (
    <div>
      <h3>Instructions:</h3>
      <ul>
        {instructions !== undefined ? (
          instructions.map((instruction, index) => (
            <li key={index}>{instruction.display_text}</li>
          ))
        ) : (
          <li>No instructions available</li>
        )}
      </ul>
    </div>
  );
};

export default Instructions;
